package com.example.beacon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleEventForm extends AppCompatActivity {

    private Button submitEventForm;
    private DatabaseReference eventRef;
    private DatabaseReference usersRef;
    private Boolean isPublic;

    private FirebaseAuth mAuth;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_form);

        mAuth = FirebaseAuth.getInstance();
        eventRef = FirebaseDatabase.getInstance().getReference().child("Events");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");



        EditText title = findViewById(R.id.title_event_box);
        DatePicker date = findViewById(R.id.date_event);
        TimePicker startTime = findViewById(R.id.start_time);
        TimePicker endTime = findViewById(R.id.end_time);
        Spinner location = findViewById(R.id.location_menu);
        EditText description = findViewById(R.id.description_box);
        CheckBox privacyCheck = findViewById(R.id.checkboxYes);
        String thisUser = mAuth.getUid();


        submitEventForm = findViewById(R.id.submitEventForm_button);

        submitEventForm.setOnClickListener(new View.OnClickListener() {
            //need to set action for click
            @Override
            public void onClick(View view) {
                //event ID for the current event being submitted
                String currentEvent = RandomID();

                String thisEventTitle = title.getText().toString();
                String thisEventDate = date.getDisplay().toString();
                String thisEventStartTime = startTime.getDisplay().toString();
                String thisEventEndTime = endTime.getDisplay().toString();

                //NEED TO RETRIEVE START AND END TIME

                if (privacyCheck.isChecked()){
                    isPublic = false;
                } else {
                    isPublic = true;
                }
                String thisEventLocation = location.getDisplay().toString();
                String thisEventDescription = description.getText().toString();

                //STORE ALL THIS INFO IN AN INSTANCE OF A CLASS AND SEND TO FIREBASE
                //Event thisEvent = new Event(thisEventTitle, isPublic, thisEventDate, thisEventStartTime, thisEventEndTime, thisEventLocation, thisEventDescription, thisUser, );

                sendToActivity(EventActivity.class);
            }

        });
        //add all campus locations
        String[] items = {"Coray Gym", "Buswell Library", "Anderson Commons", "Edman Chapel", "Lower Beamer", "Off-Campus"};

        //connects string of locations to drop down menu in xml file
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.location_menu);
        spinner.setAdapter(adapter);


    }

    //method that generates a random ID, used for events in database
    public String RandomID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private void sendToActivity(Class<?> a) { //this method changes the activity to appropriate activity
        Intent switchToNewActivity= new Intent(ScheduleEventForm.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //finish();
    }

    private void getUserName(){
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String username = snapshot.child(mAuth.getCurrentUser().getUid()).child("username").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }


}
