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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleEventForm extends AppCompatActivity {

    private Button submitEventForm;
    private DatabaseReference eventRef;
    private Boolean isPublic;

    private FirebaseAuth mAuth;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_form);

        mAuth = FirebaseAuth.getInstance();
        eventRef = FirebaseDatabase.getInstance().getReference().child("Events");

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
                //need to change check boxes for privacy to a switch that generates a boolean value
                /*
                eventRef.child(currentEvent).child("Title").setValue(title.getText().toString());
                eventRef.child(currentEvent).child("Date").setValue(date.getDisplay().toString());
                eventRef.child(currentEvent).child("Start").setValue(startTime.getText().toString());
                eventRef.child(currentEvent).child("End").setValue(endTime.getText().toString());
                eventRef.child(currentEvent).child("Location").setValue(location.getDisplay().toString());
                eventRef.child(currentEvent).child("Description").setValue(description.getText().toString());
                if (privacyCheck.isChecked()){
                    eventRef.child(currentEvent).child("Privacy").setValue("private");
                    publicStatus = false;
                } else {
                    eventRef.child(currentEvent).child("Privacy").setValue("open");
                    publicStatus = true;
                }
                 */

                String thisEventTitle = title.getText().toString();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDate thisEventDate = LocalDate.parse(date.getDisplay().toString());
                }

                //NEED TO RETRIEVE START AND END TIME
                LocalTime thisEventStartTime = LocalTime.parse(startTime.getDisplay().toString());
                LocalTime thisEventEndTime = LocalTime.parse(endTime.getDisplay().toString());

                if (privacyCheck.isChecked()){
                    isPublic = false;
                } else {
                    isPublic = true;
                }
                String thisEventLocation = location.getDisplay().toString();
                String thisEventDescription = description.getText().toString();

                //STORE ALL THIS INFO IN AN INSTANCE OF A CLASS AND SEND TO FIREBASE

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

}
