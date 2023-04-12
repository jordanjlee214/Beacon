package com.example.beacon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteEventPage extends AppCompatActivity {
    private Button  deleteEventButton;

    private FirebaseAuth mAuth;
    private DatabaseReference eventRef;

    private Query query;
    private ArrayList<String> myEvents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_event);

        mAuth = FirebaseAuth.getInstance();
        eventRef = FirebaseDatabase.getInstance().getReference().child("Events");
        deleteEventButton = findViewById(R.id.deleteEvent_button);


        setUpMyEvents();
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
/*

                ArrayList<String> myEvents = new ArrayList<>();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String item = itemSnapshot.getValue(Event.class).toString();
                    myEvents.add(item);
                }

               RecyclerView recyclerView = findViewById(R.id.deleteEventRecyclerView);
               eventAdapter adapter = new eventAdapter(myEvents);                 recyclerView.setAdapter(adapter);
               recyclerView.setLayoutManager(new LinearLayoutManager(getParent()));
*/
            setUpMyEvents();

          }

            @Override
            public void onCancelled(DatabaseError error) {
            }

        });
        RecyclerView recyclerView = findViewById(R.id.deleteEventRecyclerView);

        DeleteEventAdapter adapter = new DeleteEventAdapter(this, myEvents);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));




        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToActivity(DeleteEventPage.class);
            }     });

    };
    private void setUpMyEvents() {
        query = eventRef.orderByChild("creatorID").equalTo(mAuth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> myEvents = new ArrayList<>();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String item = itemSnapshot.getValue(Event.class).toString();
                    myEvents.add(item);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    };

    private void sendToActivity(Class<?> a) {
        Intent switchToNewActivity= new Intent(DeleteEventPage.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
