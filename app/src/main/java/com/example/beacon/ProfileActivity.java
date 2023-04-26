package com.example.beacon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageView searchButton;
    private EditText usernameField;
    private TextView closeButton;

    private FrameLayout fragmentContainer;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    private boolean fragmentDisplayed;
    private AppCompatButton backButton;

    private User searchedUser; //the user class representing the user we are trying to find
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fragmentDisplayed = false;
        fragmentContainer = (FrameLayout) findViewById(R.id.profileFragmentContainer);
        searchButton = findViewById(R.id.profileSearchButton);
        usernameField = findViewById(R.id.searchUsernameField);
        closeButton = findViewById(R.id.profileExitButton);
        closeButton.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        searchedUser = new User();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!usernameField.getText().toString().isEmpty()){
                    if(fragmentDisplayed){
                        closeFragment();
                    }
                    searchUsername(usernameField.getText().toString());
                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() { //this button closes the fragment
            @Override
            public void onClick(View view) {
                if(fragmentDisplayed){
                    Log.d("PROFILE", "frag: " + fragmentDisplayed);
                    closeFragment();
                }
            }
        });

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener((view -> sendToActivity(FriendActivity.class)));
    }

    /**
     * Switch to another activity
     * @param a activity to go to
     */
    private void sendToActivity(Class<?> a) { //this method changes the activity to appropriate activity
        Intent switchToNewActivity= new Intent(ProfileActivity.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //finish();
    }

    /*
        this helper method searches the database for the entered username and gets an ID
     */
    private void searchUsername(String username){
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean foundUsername = false;
                for(DataSnapshot id : snapshot.getChildren()){ //searches through all the user IDs
                    if(id.child("username").getValue().toString().equals(username)){ //does the username equal this
                        foundUsername = true; //we found the username!
                        searchedUser.setUserID(id.getKey().toString()); //set user object's user ID
                        Log.d("PROFILE", id.getKey().toString());
                        showProfileFragment(searchedUser.getUserID()); //display the fragment
                    }
                }
                if(!foundUsername){
                    Toast.makeText(ProfileActivity.this, "Couldn't find that user. :(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showProfileFragment(String userID){
        ProfileFragment profileFragment = ProfileFragment.newInstance(userID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.addToBackStack(null);
        transaction.add(R.id.profileFragmentContainer, profileFragment, "BLANK_FRAGMENT").commit();
        closeButton.setVisibility(View.VISIBLE);
        fragmentDisplayed = true;
    }

    private void closeFragment(){
        onBackPressed();
        fragmentDisplayed = false;
        closeButton.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeButton.setVisibility(View.GONE);

    }
}