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
    private User sender;

    /**
     * The Receiver
     * (For Firebase)
     */
    private User receiver;

    /**
     * Message to receiver
     */
    private String message;

    /**
     * Constructor
     */
    public FriendRequest(User sender, String message, User receiver){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public User getSender() {return sender;}
    public User getReceiver() {return receiver;}

    public String getSendersNickname(){return sender.getNickname();}

    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}

    public String getKey(){ //returns a key for the database
        return receiver.getUserID() + "_" + sender.getUserID();
    }

    public String getReverseKey(){
        return sender.getUserID() + "_" + receiver.getUserID();
    }
    public HashMap<String, Object> toMap(){ //returns a map to upload to the database
        HashMap<String, Object> toReturn = new HashMap<>();
        toReturn.put("message", message);
        toReturn.put("receiverUser", receiver.getUsername());
        toReturn.put("senderUser", sender.getUsername());
        return toReturn;
    }
}
