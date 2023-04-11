package com.example.beacon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button eventActivityButton, signOutButton, friendActivityButton, mapsActivityButton, setupActivityButton, profileActivityButton;
    private TextView userDataText; // a test that displays the username to show that the user has data stored
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private String currentUserID;
    public static ArrayList<Location> locDat;

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
        setupActivityButton = findViewById(R.id.setupactivity_button);
        profileActivityButton = findViewById(R.id.profileactivity_button);

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

        profileActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToActivity(ProfileActivity.class);
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