package com.example.beacon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

    private Button createEventButton, backButton, deleteEventButton, filterButton;

    private FirebaseAuth mAuth;

    private DatabaseReference eventRef;
    private String[] items = CampusLocations.sorted();

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
        filterButton = findViewById(R.id.filterButton);

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




        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.location_filter);
        spinner.setAdapter(adapter);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { filterEvents(); }
        });

        //to auto filter events by location if sent from map marker
        String methodName = getIntent().getStringExtra("methodName");
        if (methodName != null && methodName.equals("setFilter")) {
            setFilter(getIntent().getStringExtra("location"));
        }
    }

    private void sendToActivity(Class<?> a) { //this method changes the activity to appropriate activity
        Intent switchToNewActivity= new Intent(EventActivity.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //finish();
    }

    private void filterEvents(){

        Spinner location = findViewById(R.id.location_filter);
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //new event list to be displayed
                ArrayList<String> events = new ArrayList<>();

                //stores location to filter events by
                String thisLocation = (String) location.getSelectedItem();

                if (thisLocation.equals("All")){
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        String item = itemSnapshot.getValue(Event.class).toString();
                        events.add(item);
                    }
                } else {
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {

                        //retrieve location of each event on database
                        String itemLocation = itemSnapshot.getValue(Event.class).getEventLocation();

                        //if event location matches location filter, adds event to list
                        if (itemLocation.equals(thisLocation)) {
                            String item = itemSnapshot.getValue(Event.class).toString();
                            events.add(item);
                        }
                    }
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

    /**
     *
     * @param location
     */
    private void setFilter(String location) {
        if(location.equals(""))
            return;
        Spinner filter = findViewById(R.id.location_filter);
        int index = 0;
        for(; index<items.length;index++){
            if(items[index].equals(location))
                break;
        }
        assert items[index].equals(location);
        filter.setSelection(index);
        filterEvents();
    }

}