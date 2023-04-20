package com.example.beacon;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    private Button signInButton;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private BeginSignInRequest signInRequest; //manages GoogleAuthentication
    private SignInClient oneTapClient;
    private FirebaseAuth mAuth; //handles all Firebase Authentication protocols
    private DatabaseReference usersRef; //reads and writes to Firebase database

    /**these GUI items below are all for alternate sign in for testing**/
    private Button altSignInButton;
    private EditText altPasswordField, altEmailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //set up materials for a Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        //set up button
        signInButton = findViewById(R.id.signInButton);
                signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

       /**this is only for alternate sign **/
       altSignInButton = findViewById(R.id.altSignInButton);
       altEmailField = findViewById(R.id.altLogInEmailField);
       altPasswordField = findViewById(R.id.altLogInPWField);
       altSignInButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               altSignIn();
           }
       });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    private void signIn(){ //helper method to carry out function to sign-in
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                });
    }

    /*code pulled from Google's tutorial on authenticating Google accounts:
    https://firebase.google.com/docs/auth/android/google-signin?utm_source=studio#java_2
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);

                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "signInWithCredential:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            checkWheaton(user);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                                        }
                                    }
                                });
                        Log.d(TAG, "Got ID token.");
                    }
                } catch (ApiException e) {
                    // ...
                }
                break;
        }
    }

    /*this method will fully authenticate the account if the Google account is
    a Wheaton student account. the account will be deleted if it is not. the user
    will be asked to try again */
    private void checkWheaton(FirebaseUser user){
        if(isWheatonEmail(user.getEmail())){  //if its a wheaton email, put it's info on the database if we need to
            usersRef.addValueEventListener(new ValueEventListener() { //read the database
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(mAuth.getCurrentUser().getUid()).exists()){ //if the user is already in database
                        Log.d(TAG, "EXISTENCE: User already exists ");
                        getNotificationToken();
                        sendToMain(); //just send to home screen
                    }
                    else {
                        Log.d(TAG, "EXISTENCE: User must be stored in database ");
                        setUpUserData(user); //if not, store user data on database
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{ //if this isn't a wheaton email, delete the user and have them sign in again
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User account deleted.");
                            }
                            Toast.makeText(LoginActivity.this, "You must sign in with a Wheaton (my.wheaton.edu) Google account. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    //helper method to check if an email is a my.wheaton.edu account
    private boolean isWheatonEmail(String email){
        String domain = email.substring(email.indexOf('@')+1);
        //return domain.equals("my.wheaton.edu");
        return true; //i put this there so we can allow Non-Wheaton accounts for the sake of testing
    }

    //sets up basic user data in the Firebase Database such as username and user ID
    private void setUpUserData(FirebaseUser user){
        User basicUser = new User();
        basicUser.setUserID(mAuth.getCurrentUser().getUid());

        String email = user.getEmail();
        String username = email.substring(0, email.indexOf('@'));
        basicUser.setUsername(username);

        String firstName = user.getDisplayName().substring(0, user.getDisplayName().indexOf(" "));
        String lastName = user.getDisplayName().substring(user.getDisplayName().indexOf(" ")+1);
        basicUser.setFirstName(firstName);
        basicUser.setLastName(lastName);
        basicUser.setPhotoURL(user.getPhotoUrl().toString());

        usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(basicUser.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "User data in database.", Toast.LENGTH_SHORT).show();
                    getNotificationToken();
                    sendToMain();
                }
                else{
                    String message = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, "Uh oh! An error occurred: " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //the following methods will send the user to other activities
    private void sendToMain(){
        Intent sendToMain = new Intent(LoginActivity.this, MainActivity.class);
        sendToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendToMain);
        finish();
    }

    /**alternate way of setting up user accounts in database)**/
    private void alternateSetUpUserData(FirebaseUser user){
        User basicUser = new User();
        basicUser.setUserID(mAuth.getCurrentUser().getUid());

        String email = user.getEmail();
        String username = email.substring(0, email.indexOf('@'));
        basicUser.setUsername(username);

        basicUser.setFirstName("Philip");
        basicUser.setLastName("Ryken");

        usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(basicUser.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "User data in database.", Toast.LENGTH_SHORT).show();
                    sendToMain();
                }
                else{
                    String message = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, "Uh oh! An error occurred: " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getNotificationToken(){ //gets a notification token and puts it on the database under the user
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        usersRef.child(mAuth.getCurrentUser().getUid()).child("notificationKeys").child(token).setValue(true);
                    }
                });
    }



    /** this method is for alternate sign ins for testing.
     * the Google authentication doesn't always work, so this will be regular email
     * authentication that we will use before fixing the GoogleAuth issue **/
    private void altSignIn() {
        if(!altEmailField.getText().toString().isEmpty() && !altPasswordField.getText().toString().isEmpty()) {
            String email = altEmailField.getText().toString();
            String password = altPasswordField.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        sendToMain();
                    }
                    else{
                        String msg = task.getException().getMessage();
                        if(msg.equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser altUser = mAuth.getCurrentUser();
                                        alternateSetUpUserData(altUser);
                                    }
                                    else{
                                        String msg = task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }
                }
            });
        }
    }

}