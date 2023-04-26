package com.example.beacon;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

/**
 * To Model a Friend Request
 * @author Bonnie Rilea
 * CSCI 335
 */
public class FriendRequest {
    /**
     * The Sender
     */
    private User senderUser;

    /**
     * The Sender's ID
     */
    private String senderID;

    /**
     * The Receiver
     * (For Firebase)
     */
    private User receiverUser;

    /**
     * The Receiver's ID
     */
    private String receiverID;

    /**
     * Message to receiver
     */
    private String message;

    /**
     * Constructor
     */
    public FriendRequest(User senderUser, String message, User receiverUser){
        this.senderUser = senderUser;
        senderID = senderUser.getUserID();
        this.receiverUser = receiverUser;
        receiverID = receiverUser.getUserID();
        this.message = message;
    }

    public User getSenderUser() {return senderUser;}
    public User getReceiverUser() {return receiverUser;}

    public String getSendersUsername(){return senderUser.getUsername();}

    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}

    public String getKey(){ //returns a key for the database
        return receiverUser.getUserID() + "_" + senderUser.getUserID();
    }

    public String getReverseKey(){
        return senderUser.getUserID() + "_" + receiverUser.getUserID();
    }
    public HashMap<String, Object> toMap(){ //returns a map to upload to the database
        HashMap<String, Object> toReturn = new HashMap<>();
        toReturn.put("message", message);
        toReturn.put("receiverUser", receiverUser.getUsername());
        toReturn.put("senderUser", senderUser.getUsername());
        toReturn.put("receiverID", receiverUser.getUserID());
        toReturn.put("senderID", senderUser.getUserID());
        return toReturn;
    }

    public static FriendRequest buildRequestFromSnapshot(DataSnapshot snapshot){
        User senderUser = new User();
        senderUser.setUserID(snapshot.child("senderID").getValue().toString());
        senderUser.setUsername(snapshot.child("senderUser").getValue().toString());
        User receiverUser = new User();
        receiverUser.setUserID(snapshot.child("receiverID").getValue().toString());
        receiverUser.setUsername(snapshot.child("receiverUser").getValue().toString());
        FriendRequest newRequest = new FriendRequest(senderUser, snapshot.child("message").getValue().toString(), receiverUser);
        return newRequest;
    }
}
