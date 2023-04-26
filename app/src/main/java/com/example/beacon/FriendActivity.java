package com.example.beacon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        xList.setLayoutManager(new LinearLayoutManager(this));
        xList.setAdapter(new UserAdaptor("~ Press a button ~"));
    }

    public void showFriends(View view){
        //Step 1: get database's reference
        friendlist.clear();
        if(friendlist.isEmpty()){ xList.setAdapter(emptyListAdaptor); }
        friendsReference = database.getReference().child("Friends").child(current.getUid());
        //Step 2: add listener
        friendsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User newUser = User.buildUserFromFriendSnapshot(snapshot, snapshot.getKey());
                if(!listHasUser(friendlist, newUser))
                    friendlist.add(newUser);
                friendAdaptor.notifyDataSetChanged();
                if(friendlist.isEmpty()){ xList.setAdapter(emptyListAdaptor); }else xList.setAdapter(friendAdaptor);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User changedUser = User.buildUserFromFriendSnapshot(snapshot, snapshot.getKey());
                if(listHasUser(friendlist, changedUser)) {
                    friendlist.remove(changedUser);
                    friendlist.add(changedUser);
                }
                friendAdaptor.notifyDataSetChanged();
                if(friendlist.isEmpty()){ xList.setAdapter(emptyListAdaptor); }else xList.setAdapter(friendAdaptor);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User removedUser = User.buildUserFromFriendSnapshot(snapshot, snapshot.getKey());
                //find the user who's id matches removedUser's
                for(User u : friendlist) {
                    if(u.getUserID().equals(removedUser.getUserID())) friendlist.remove(removedUser);
                }
                friendAdaptor.notifyDataSetChanged();
                if(friendlist.isEmpty()){ xList.setAdapter(emptyListAdaptor); }else xList.setAdapter(friendAdaptor);
            }
            @Override //Do priorities change?
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        //Step 3: set up adapter

    }

    //remove friends

    public void showRequests(View view){
        requestsReference = database.getReference().child("FriendRequests");
        requests.clear();
        requestsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot requestChild : snapshot.getChildren()){ //go through each request
                    if(isReceivingUser(requestChild.getKey())){
                        FriendRequest request = FriendRequest.buildRequestFromSnapshot(requestChild);
                        if(!listHasRequest(requests, request)){
                            requests.add(request);
                        }
                        xList.setAdapter(rAdaptor);
                    }
                }
                if(requests.isEmpty()) xList.setAdapter(emptyListAdaptor); //if still empty, there's nothing

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        if(requests.isEmpty()) xList.setAdapter(emptyListAdaptor);
//        else xList.setAdapter(rAdaptor);
    }

   /* public void showBlocked(View view){
        blockedReference = database.getReference().child("Blocked").child(current.getUid());
        blockedReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User newUser = User.buildUserFromFriendSnapshot(snapshot, snapshot.getKey());
                if(!blocked.contains(newUser))
                    blocked.add(newUser);
                blockedAdaptor.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User changedUser = User.buildUserFromFriendSnapshot(snapshot, snapshot.getKey());
                if(listHasUser(blocked, changedUser)) {
                    blocked.remove(changedUser);
                    blocked.add(changedUser);
                    blockedAdaptor.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User removedUser = User.buildUserFromFriendSnapshot(snapshot, snapshot.getKey());
                blocked.remove(removedUser);
                blockedAdaptor.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        if(blocked.isEmpty()){ xList.setAdapter(emptyListAdaptor); }
        else xList.setAdapter(blockedAdaptor);
    }*/

    //user search feature
    public void toProfileFragment(View view){
        sendToActivity(ProfileActivity.class);
    }

    /* Helpers for show friends*/

    private boolean listHasUser(List<User> userList, User u){
        for(User current : userList){
            if(current.getUserID().equals(u.getUserID()))
                return true;
        }
        return false;
    }

    private boolean listHasRequest(List<FriendRequest> requestList, FriendRequest u){
        for(FriendRequest current : requestList){
            if(current.getSenderUser().getUserID().equals(u.getSenderUser().getUserID()) && current.getReceiverUser().getUserID().equals(u.getReceiverUser().getUserID()))
                return true;
        }
        return false;
    }

    /**
     * Checks if current user is the receiving user of the friend request.
     */
    private boolean isReceivingUser(String key){
        String receiverID = key.substring(0, key.indexOf('_'));
     //   Log.d("KEY", receiverID + " and " + current.getUid());
        return receiverID.equals(current.getUid());
    }

    /**
     * Go home
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendToActivity(MainActivity.class);
    }

    private void sendToActivity(Class<?> a) { //this method changes the activity to appropriate activity
        Intent switchToNewActivity= new Intent(FriendActivity.this, a);
        startActivity(switchToNewActivity);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //finish();
    }

}