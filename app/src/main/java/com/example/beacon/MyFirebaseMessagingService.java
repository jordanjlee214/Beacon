package com.example.beacon;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private DatabaseReference friendsRef;
    private String currentUserID;
    private FirebaseMessaging firebaseMessaging;

    public MyFirebaseMessagingService(){
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            currentUserID = mAuth.getCurrentUser().getUid();
        }
        firebaseMessaging = FirebaseMessaging.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        Log.i("jordanjlee", "message received!");

    }


    /**
     * Subscribes from the notification channel of the user with the given ID.
     * @param userID
     */
    public void subscribeToUser(String userID){
        firebaseMessaging.subscribeToTopic(userID)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d("TAG", msg);
                    }
                });
    }

    /**
     * Unsubscribes from all channels.
     */
    public void unsubscribeAll(){
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot user : snapshot.getChildren()){
                    String id = user.getKey();
                    unsubscribeFromUser(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Unsubscribes from the notification channel of the user with the given ID.
     * @param userID
     */
    public void unsubscribeFromUser(String userID){
        firebaseMessaging.unsubscribeFromTopic(userID)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d("TAG", msg);
                    }
                });
    }

    /*
        Searches through the current user's friends and subscribes to their notification channel.
     */
    public void subscribeToFriends(){
        friendsRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot friendSnapshot : snapshot.getChildren()){
                    String friendID = friendSnapshot.getKey();
                    subscribeToUser(friendID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
