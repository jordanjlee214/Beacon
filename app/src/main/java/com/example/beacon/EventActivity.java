package com.example.beacon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class EventActivity extends AppCompatActivity {

    private Button createEventButton, backButton, deleteEventButton;

    private FirebaseAuth mAuth;

    private DatabaseReference eventRef;

    //ArrayList<String> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mAuth = FirebaseAuth.getInstance();
        eventRef = FirebaseDatabase.getInstance().getReference().child("Events");

        createEventButton = findViewById(R.id.createEvent_button);
        backButton = findViewById(R.id.backButton);
        deleteEventButton = findViewById(R.id.deleteEvent_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToActivity(MainActivity.class);
            }
        });

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToActivity(ScheduleEventForm.class);
            }
        });

        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { sendToActivity(DeleteEventPage.class); }
        });


        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //ArrayList<String> items = new ArrayList<>();
                ArrayList<String> events = new ArrayList<>();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String item = itemSnapshot.getValue(Event.class).toString();
                    events.add(item);
                }

                RecyclerView recyclerView = findViewById(R.id.eventRecyclerView);
                eventAdapter adapter = new eventAdapter(events);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getParent()));

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });


    }

    private void sendToActivity(Class<?> a) { //this method changes the activity to appropriate activity
        Intent switchToNewActivity= new Intent(EventActivity.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //finish();
    }

}