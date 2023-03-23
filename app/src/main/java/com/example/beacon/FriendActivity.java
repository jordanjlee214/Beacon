package com.example.beacon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * The Friend Activity Control
 * Functionality: Show Friends, Blocked, Requests
 * @author Bonnie Rilea
 * CSCI 335
 */

public class FriendActivity extends AppCompatActivity {

    //The RecyclerView (doesn't/shouldn't change)
    private RecyclerView userList;
    //instance of Database (doesn't/shouldn't change)
    private FirebaseDatabase database;
    //the current user
    FirebaseUser current;
    //the reference paths for each lists of data for current
    DatabaseReference friendsReference, blockedReference, requestsReference;
    //The "Nothing to see here!" String
    final private String nothingHere = String.valueOf(R.string.friend_page_noone);
    //to make contexts simpler?
    Context important = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //First, "create" UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        //Next, get instance of authenticator, database, and current user, and current's database references
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        current = mAuth.getCurrentUser();

        //set up RecyclerView
        userList = findViewById(R.id.listOfX);
        String defaultMessage = String.valueOf(R.string.friend_page_default);
        userList.setAdapter(new RecyclerViewObjectAdaptor(this, defaultMessage));
    }

    public void showFriends(View view){
        //Step 1: get database's reference
        friendsReference = database.getReference("server/Users/"+ current.getUid() + "/friendList");
        //Step 2: add listener
        friendsReference.addValueEventListener(initializeValueEventListener());
        //Step 3: set up adapter
        // check for null: null means there is nothing there
        if(friendsReference.get().getResult().getValue() == null)
            userList.setAdapter(new RecyclerViewObjectAdaptor(this, nothingHere));
        else userList.setAdapter(new RecyclerViewObjectAdaptor(this, friendsReference.get().getResult()));
    }

    public void showRequests(View view){
        requestsReference = database.getReference("server/Users/"+ current.getUid() + "/requestList");
        requestsReference.addValueEventListener(initializeValueEventListener());
        if(requestsReference.get().getResult().getValue() == null)
            userList.setAdapter(new RecyclerViewObjectAdaptor(this, nothingHere));
        else userList.setAdapter(new RecyclerViewObjectAdaptor(this, requestsReference.get().getResult()));
    }

    public void showBlocked(View view){
        blockedReference = database.getReference("server/Users/"+ current.getUid() + "/blockedList");
        requestsReference.addValueEventListener(initializeValueEventListener());
        if(blockedReference.get().getResult().getValue() == null)
            userList.setAdapter(new RecyclerViewObjectAdaptor(this, nothingHere));
        else userList.setAdapter(new RecyclerViewObjectAdaptor(this, blockedReference.get().getResult()));
    }

    //TODO set up searchbar

    //Helper for ValueEventListener
    private ValueEventListener initializeValueEventListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot == null)
                    userList.setAdapter(new RecyclerViewObjectAdaptor(important, nothingHere));
                else
                    userList.setAdapter(new RecyclerViewObjectAdaptor(important, snapshot));
            }

            @SuppressLint("ResourceType")
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(findViewById(R.layout.activity_friend),
                        "Something went wrong, please try again! " + error, Snackbar.LENGTH_SHORT).show();
            }
        };
    }
}