package com.example.beacon;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;


import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{

    private AppCompatImageButton eventActivityButton, friendActivityButton, mapsActivityButton, setupActivityButton;
    private Button signOutButton;
    private TextView userDataText; // a test that displays the username to show that the user has data stored
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private String currentUserID;
    public static ArrayList<Location> locDat;
    private MyFirebaseMessagingService firebaseMessagingService;

    //TODO fix SignInClient
    private SignInClient oneTapClient;

    private String[] notiPermissions = new String[]{
            Manifest.permission.POST_NOTIFICATIONS
    };

    private boolean permission_post_notification =false;

    //result launcher to request notification permissions
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                   permission_post_notification = true;
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                    permission_post_notification = false;
                    Toast.makeText(MainActivity.this, "Permission for notifiactions is denied. Please enable to receive notifcations.", Toast.LENGTH_SHORT).show();
               showPermissionDialog("Permission Dialog");
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        //TODO fix Identity
        oneTapClient = Identity.getSignInClient(this);

        //set up UI objects and connect them to XML layout
        eventActivityButton = findViewById(R.id.eventactivity_button);
        signOutButton = findViewById(R.id.signOut_button);
        friendActivityButton = findViewById(R.id.friendactivity_button);
        mapsActivityButton = findViewById(R.id.mapactivity_button);
        userDataText = findViewById(R.id.userDataTest);
        setupActivityButton = findViewById(R.id.setupactivity_button);

        //set up listeners for each button
        //each listener sends user to the corresponding activity
        eventActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToActivity(EventActivity.class);
            }
        });

        friendActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToActivity(FriendActivity.class);
            }
        });

        mapsActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToActivity(MapsActivity.class);
            }
        });

        setupActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToActivity(SetupActivity.class);
            }
        }
        );

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = FirebaseMessaging.getInstance().getToken().toString();
                removeToken();
                mAuth.signOut();
                oneTapClient.signOut(); //TODO fix signOut()
                sendToActivity(LoginActivity.class);
                finish();
            }
        });

        //Buildings setup
        locDat = new ArrayList<Location>();
        locDat.add(new Location("Anderson Commons", new LatLng(41.86911878272806, -88.09712665357651)));
        locDat.add(new Location("Meyer Science Center", new LatLng(41.869734693386576, -88.0960876587158)));
        locDat.add(new Location("Smith-Traber", new LatLng(41.87073447303972, -88.09464504447187)));
        locDat.add(new Location("Chrouser Sports", new LatLng(41.870976913830596, -88.0966584645037)));
        locDat.add(new Location("Fischer", new LatLng(41.87280642014386, -88.09680866820558)));
        locDat.add(new Location("North Harrison Hall", new LatLng(41.872638651007705, -88.09764551742585)));
        locDat.add(new Location("McManis-Evans", new LatLng(41.87005814636335, -88.09794592481995)));
        locDat.add(new Location("Campus Store", new LatLng(41.86889968159881, -88.09795665364001)));
        locDat.add(new Location("College Ave Apartments", new LatLng(41.86819660309276, -88.0948882065569)));
        locDat.add(new Location("Terrace Apartments", new LatLng(41.869546826554284, -88.0899851284701)));
        locDat.add(new Location("Saint & Elliot Apartments", new LatLng(41.869794497585, -88.0923454723106)));
        locDat.add(new Location("Michigan-Crescent Apartments", new LatLng(41.86605536180369, -88.09593963243061)));
        locDat.add(new Location("Sports Fields", new LatLng(41.867469547852814, -88.09532808875328)));
        locDat.add(new Location("BGH", new LatLng(41.86667856630517, -88.09941577522754)));
        locDat.add(new Location("Blanchard", new LatLng(41.86845226889066, -88.09958743662516)));
        locDat.add(new Location("Adams", new LatLng(41.86913137621643, -88.09984492869775)));
        locDat.add(new Location("Williston Hall", new LatLng(41.868929778746, -88.09820536242233)));
        locDat.add(new Location("Memorial Student Center", new LatLng(41.86918752932761, -88.09873709265132)));
        locDat.add(new Location("Armerding", new LatLng(41.87056218155288, -88.09837090108482)));
        locDat.add(new Location("Wyngarden & Schell", new LatLng(41.86992715567431, -88.09888758233473)));
        locDat.add(new Location("Buswell Library", new LatLng(41.86999439400873, -88.09961494916328)));
        locDat.add(new Location("Edman Chapel", new LatLng(41.86994583299493, -88.10065332801665)));
        locDat.add(new Location("Wade Center", new LatLng(41.87058376852388, -88.10108889030053)));
        locDat.add(new Location("Public Library", new LatLng(41.86681180911569, -88.10469784804484)));

        checkNotificationPermission();
    }
    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //this is a test: if user is signed in, it displays their name
            currentUserID = currentUser.getUid();
            firebaseMessagingService = new MyFirebaseMessagingService();
            firebaseMessagingService.unsubscribeAllThenSubToFriends(); //unsubscribes from all friends to stay updated
            displayUserData();

        }
        else{ //if user isn't signed in, send them to the log-in page
            sendToActivity(LoginActivity.class);
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void sendToActivity(Class<?> a) { //this method changes the activity to appropriate activity
        Intent switchToNewActivity= new Intent(MainActivity.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //finish();
    }

    private void displayUserData(){      //display user information from the database on the text
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = snapshot.child(currentUserID).child("firstName").getValue().toString();
                String lastName = snapshot.child(currentUserID).child("lastName").getValue().toString();
                String username = snapshot.child(currentUserID).child("username").getValue().toString();
                userDataText.setText("Welcome, " + firstName + " " + lastName + "!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkNotificationPermission(){
        if(!permission_post_notification){
            askNotificationPermission();
            Log.i("permission", "not enabled yet");
        }
        else{
            Toast.makeText(MainActivity.this, "Notification permissions granted.", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Notification checking code comes from this tutorial:
     * https://www.youtube.com/watch?v=_UubmZ4qJlI
     * and
     * https://firebase.google.com/docs/cloud-messaging/android/client
     */
    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, notiPermissions[0]) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
                Log.i("Permission: ", "it's granted!");
            }
            else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    // TODO: display an educational UI explaining to the user the features that will be enabled
                    //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                    //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                    //       If the user selects "No thanks," allow the user to continue without notifications.


                } else {
                    // Directly ask for the permission

                }
                Log.i("Permission: ", "requestPerm");
                requestPermissionLauncher.launch(notiPermissions[0]);
            }
        }
    }

    private void showPermissionDialog(String msg){
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Permission for Notifications")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent rIntent = new Intent();
                        rIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        rIntent.setData(uri);
                        startActivity(rIntent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void removeToken(){
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
                        HashMap<String, Object> keysMap = new HashMap<>();
                        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child("notificationKeys").exists()){
                                    for(DataSnapshot key : snapshot.child("notificationKeys").getChildren()){
                                        if(!key.getKey().equals(token))
                                            keysMap.put(key.getKey(), true);
                                    }
                                    if(!keysMap.isEmpty()) {
                                        usersRef.child(currentUserID).child("notificationKeys").updateChildren(keysMap);
                                    }
                                    else{
                                        usersRef.child(currentUserID).child("notificationKeys").setValue(null);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

}