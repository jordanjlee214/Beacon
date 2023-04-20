package com.example.beacon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The Friend Activity Control
 * Functionality: Show Friends, Blocked, Requests
 * @author Bonnie Rilea
 * CSCI 335
 */

public class FriendActivity extends AppCompatActivity {

    //The RecyclerView (doesn't/shouldn't change)
    private RecyclerView xList;
    //instance of Database (doesn't/shouldn't change)
    private FirebaseDatabase database;
    //the current user
    FirebaseUser current;
    //the reference paths for each lists of data for current
    DatabaseReference friendsReference, blockedReference, requestsReference;
    //Adaptors
    UserAdaptor friendAdaptor, blockedAdaptor, emptyListAdaptor;
    RequestAdaptor rAdaptor;
    //User's Lists
    List<User> friendlist, blocked;
    List<FriendRequest> requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //First, "create" UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        //Next, get instance of authenticator, database, and current user,
        // and current's database references
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        current = mAuth.getCurrentUser();

        //set up RecyclerView
        xList = findViewById(R.id.listOfX);

        //Initialize Lists
        friendlist = new ArrayList<User>();
        blocked = new ArrayList<User>();
        requests = new ArrayList<FriendRequest>();
        //Create Adaptors
        friendAdaptor = new UserAdaptor(friendlist);
        blockedAdaptor = new UserAdaptor(blocked);
        emptyListAdaptor = new UserAdaptor("Nothing to see here!");
        rAdaptor = new RequestAdaptor(requests);

        //rAdaptor = new RequestAdaptor(requests);
        //Set Adaptors for each button
        xList.setLayoutManager(new LinearLayoutManager(this));
    }

    //TODO Change childEventListeners to valueEventListeners

    public void showFriends(View view){
        //Step 1: get database's reference
        friendsReference = database.getReference().child("Friends").child(current.getUid());
        //Step 2: add listener
        friendsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User newUser = snapshot.getValue(User.class);
                if(!friendlist.contains(newUser))
                    friendlist.add(newUser);
                friendAdaptor.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User changedUser = snapshot.getValue(User.class);
                if(friendlist.contains(changedUser)) {
                    friendlist.remove(changedUser);
                    friendlist.add(changedUser);
                }
                friendAdaptor.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User removedUser = snapshot.getValue(User.class);
                friendlist.remove(removedUser);
                friendAdaptor.notifyDataSetChanged();
            }
            @Override //Do priorities change?
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        //Step 3: set up adapter
        if(friendlist.isEmpty()){ xList.setAdapter(emptyListAdaptor); }
        else xList.setAdapter(friendAdaptor);
    }

    public void showRequests(View view){
        requestsReference = database.getReference().child("FriendRequests");
    }

    public void showBlocked(View view){
        blockedReference = database.getReference().child("Blocked").child(current.getUid());
        blockedReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User newUser = snapshot.getValue(User.class);
                if(!friendlist.contains(newUser))
                    friendlist.add(newUser);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User changedUser = snapshot.getValue(User.class);
                if(friendlist.contains(changedUser)) {
                    friendlist.remove(changedUser);
                    friendlist.add(changedUser);
                }
                friendAdaptor.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User removedUser = snapshot.getValue(User.class);
                friendlist.remove(removedUser);
                friendAdaptor.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        if(blocked.isEmpty()){ xList.setAdapter(emptyListAdaptor); }
        else xList.setAdapter(blockedAdaptor);
    }

    //TODO Connect searchbar

}