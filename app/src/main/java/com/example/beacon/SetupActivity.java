package com.example.beacon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetupActivity extends AppCompatActivity {

    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserID;

    //UI elements
    private EditText firstNameField, lastNameField, usernameField, nicknameField, graduationField, birthdayField;
    private Button maleField, femaleField, saveProfileButton;
    private Spinner majorField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //initialize Firebase objects
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        firstNameField = findViewById(R.id.setUpFirstNameField);
        lastNameField = findViewById(R.id.setUpLastNameField);
        usernameField = findViewById(R.id.setUpUsernameField);
        nicknameField = findViewById(R.id.setUpNicknameField);
        graduationField = findViewById(R.id.setUpGradYearField);
        birthdayField = findViewById(R.id.setUpBirthdayField);
        maleField = findViewById(R.id.setUpMaleField);
        femaleField = findViewById(R.id.setUpFemaleField);
        saveProfileButton = findViewById(R.id.setUpSaveButton);
        majorField = findViewById(R.id.setUpMajorField);

        //set up major field spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, MajorData.wheatonMajors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorField.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null){ //if user isn't signed in, send to login page
            sendToActivity(LoginActivity.class);
            finish();
        }
        else{ //if user is signed in, save ID
            currentUserID = currentUser.getUid();
        }

    }

    private void sendToActivity(Class<?> a) { //this method changes the activity to appropriate activity
        Intent switchToNewActivity= new Intent(SetupActivity.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //finish();
    }

}