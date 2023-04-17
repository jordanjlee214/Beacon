package com.example.beacon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
    DatabaseReference friendsReference, blockedReference, requestsReference, usersRef;
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
        usersRef = database.getReference().child("Users");

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
        //TODO Code RequestAdaptor

        //rAdaptor = new RequestAdaptor(requests);
        //Set Adaptors for each button
        xList.setLayoutManager(new LinearLayoutManager(this));
    }

    //TODO Change childEventListeners to valueEventListeners

    public void showFriends(View view){
        //Step 1: get database's reference
        friendsReference = database.getReference().child("Friends").child(current.getUid());

        //if its empty, show empty message
        if(friendlist.isEmpty()){ xList.setAdapter(emptyListAdaptor); }
        else xList.setAdapter(friendAdaptor);

        //Step 2: Set up listener
        friendsReference.addValueEventListener(new ValueEventListener() { //read the Friends branch in database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot friendChild : snapshot.getChildren()){ //iterate and visit each friend
                    //the key of each friendChild is the ID of that user
                    usersRef.addValueEventListener(new ValueEventListener() { //access that friend's data in the User branch
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String friendID = friendChild.getKey().toString();
                            User newUser = new User();
                            newUser.buildUserFromSnapshot(snapshot, friendID); //fill user object with database info
                            if(!hasUser(newUser)) //must check if user is already in list
                                friendlist.add(newUser); //if user isn't already in adapter's list, add it
                            xList.setAdapter(friendAdaptor); //since we know there is at least 1 user, set adapter to friend adapter
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(friendlist.isEmpty()) //if friends list is STILL empty even after reading database
                    xList.setAdapter(emptyListAdaptor); //show empty message


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*public void showRequests(View view){
        //TODO code up
    }*/

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
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot,@Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        if(blocked.isEmpty()){ xList.setAdapter(emptyListAdaptor); }
        else xList.setAdapter(blockedAdaptor);
    }

    private void sendToActivity(Class<?> a) { //this method changes the activity to appropriate activity that you put in the argument
        Intent switchToNewActivity= new Intent(FriendActivity.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //finish();
    }

    /**
     * Checks if friendlist already has a User, does this by checking
     * existence of a user ID.
     */
    private boolean hasUser(User checkUser){
        for(User u : friendlist){
            if(u.getUserID().equals(checkUser.getUserID()))
                return true;
        }
        return false;
    }
    //TODO Connect searchbar

}