package com.example.beacon;

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

    public String getSendersNickname(){return senderUser.getNickname();}

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
        return toReturn;
    }
}
