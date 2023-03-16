package com.example.beacon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.beacon.friend_activity.FriendActivity;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button eventActivityButton, signOutButton, friendActivityButton, mapsActivityButton;
    private TextView userDataText; // a test that displays the username to show that the user has data stored
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private String currentUserID;

    //TODO fix SignInClient
    private SignInClient oneTapClient;
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

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                oneTapClient.signOut(); //TODO fix signOut()
                sendToActivity(LoginActivity.class);
                finish();
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //this is a test: if user is signed in, it displays their name
            currentUserID = currentUser.getUid();
            displayUserData();
        }
        else{ //if user isn't signed in, send them to the log-in page
            sendToActivity(LoginActivity.class);
            finish();
        }
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
                userDataText.setText("Welcome, " + firstName + " " + lastName + "!\n" + "Your username is: " + username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}