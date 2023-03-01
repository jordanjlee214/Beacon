package com.example.beacon;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ScheduleEventForm extends AppCompatActivity {

    private Button submitEventForm;
    private DatabaseReference eventRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_form);
        eventRef = FirebaseDatabase.getInstance().getReference().child("Events");

        submitEventForm = findViewById(R.id.submitEventForm_button);

        submitEventForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendToActivity(ScheduleEventForm.class);
            }
        });

        String[] items = {"Coray", "Buswell", "Anderson Commons", "Edman", "Lower Beamer", "Off-Campus"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.location_menu);
        spinner.setAdapter(adapter);
    }
}
