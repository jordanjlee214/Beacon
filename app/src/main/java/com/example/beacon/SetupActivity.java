package com.example.beacon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetupActivity extends AppCompatActivity {

    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserID;

    //UI elements
    private EditText firstNameField, lastNameField, usernameField, nicknameField, graduationField, birthdayField;
    private Button maleField, femaleField, saveProfileButton;
    private Spinner majorField;
    
    private final int NONE = 0, MALE = 1, FEMALE = 2;
    private int genderState = NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //initialize Firebase objects
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        //set up UI objects
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, MajorData.wheatonMajors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorField.setAdapter(adapter);
        
        //set up gender buttons
        maleField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genderState = MALE;
                maleField.setTextColor(Color.WHITE);
                femaleField.setTextColor(Color.BLACK);
            }
        });
        femaleField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genderState = FEMALE;
                femaleField.setTextColor(Color.WHITE);
                maleField.setTextColor(Color.BLACK);
            }
        });

        //TODO: set up first name, last name, username
        firstNameField.setEnabled(false);
        lastNameField.setEnabled(false);
        usernameField.setEnabled(false);

        firstNameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SetupActivity.this, "You cannot change your first name, as it is linked to your Wheaton account.", Toast.LENGTH_SHORT).show();
            }
        });
        lastNameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SetupActivity.this, "You cannot change your last name, as it is linked to your Wheaton account.", Toast.LENGTH_SHORT).show();
            }
        });
        usernameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SetupActivity.this, "You cannot change your username, as it is linked to your Wheaton account.", Toast.LENGTH_SHORT).show();
            }
        });

        //TODO: empty text field when you click on it

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

            usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    firstNameField.setText(snapshot.child("firstName").getValue().toString());
                    lastNameField.setText(snapshot.child("lastName").getValue().toString());
                    usernameField.setText(snapshot.child("username").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void sendToActivity(Class<?> a) { //this method changes the activity to appropriate activity
        Intent switchToNewActivity= new Intent(SetupActivity.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //finish();
    }
    
    private boolean checkFields(){ //make sure all fields are filled
        return (!nicknameField.getText().toString().equals("Nickname") && !nicknameField.getText().toString().isEmpty()) &&
               (isInteger(graduationField.getText().toString()) && Integer.parseInt(graduationField.getText().toString()) > 2022 &&
               genderState == NONE &&
                       majorField.getSelectedItem() != null);


    }

    private boolean isInteger(String s){
        try{
            Integer.parseInt(s.trim());
        }
        catch(NumberFormatException e){
            return false;
        }
        return true;
    }

    private boolean isBirthday(String s){
        String b = s.trim();
        return b.length() == 10 && b.charAt(2) == '/' && b.charAt(5) == '/' && isInteger(b.substring(0, 2)) && isInteger(b.substring(3, 5)) && isInteger(b.substring(6, 10));
    }



}