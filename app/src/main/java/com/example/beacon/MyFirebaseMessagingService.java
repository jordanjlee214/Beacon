package com.example.beacon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * Handle incoming messages and notifications through this method
     * Referred to the following online tutorial:
     *
     * https://medium.com/nybles/sending-push-notifications-by-using-firebase-cloud-messaging-249aa34f4f4c
     * https://www.youtube.com/watch?v=e9llz2TXBz8
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.i("jordanjlee", "message received!");
        super.onMessageReceived(remoteMessage);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String CHANNEL_ID = "MESSAGE";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Message Notification",
                NotificationManager.IMPORTANCE_HIGH
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Context context;
        Notification.Builder notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
        NotificationManagerCompat.from(getApplicationContext()).notify(123, notification.build());



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
                        String msg = "Subscribed: " + userID;
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
    public void unsubscribeAllThenSubToFriends(){
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot user : snapshot.getChildren()){
                    String id = user.getKey();
                    unsubscribeFromUser(id);
                }
                subscribeToFriends();
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
                        String msg = "Unsubscribed";
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

    /**
     * Sends a ping notification to the topic of the current userID.
     * This means users subscribed to the current user's topic (which are friends) will
     * receive a notification with the current ping information.
     *
     * Followed this StackOverflow thread:
     * https://stackoverflow.com/questions/55948318/how-to-send-a-firebase-message-to-topic-from-android
     *
     * and this Google tutorial:
     * https://firebase.google.com/docs/cloud-messaging/android/send-multiple
     */
    public void sendNotification(String topic, Object title, Object body, Context context){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue mRequestQue = Volley.newRequestQueue(context);

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + topic);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", title);
            notificationObj.put("body", body);
            //replace notification with data when went send data
            json.put("notification", notificationObj);

            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    response -> Log.d("MUR", "onResponse: "),
                    error -> Log.d("MUR", "onError: " + error.networkResponse)
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
               //     String key = "key=" + new String(String.valueOf(R.string.messagingServerKey));
                    String key = "key=AAAAUro6Is0:APA91bH-Hwl0BbXevxhKEECxEFoc6odhZFNN3lGoKVoipA8V--fyiK_M1GWY785Qk8f8jFcnr4zesbkjlLD99RStpOEFIy5_TC_GGKWLpxYTbiTc22u6FeXrtXrztA7houzINaPOV3fQ";
                    header.put("authorization", key);
                    return header;
                }
            };


            mRequestQue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
