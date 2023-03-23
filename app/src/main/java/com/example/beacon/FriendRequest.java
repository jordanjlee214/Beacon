package com.example.beacon;

/**
 * To Model a Friend Request
 * @author Bonnie Rilea
 * CSCI 335
 */
public class FriendRequest {
    /**
     * The Sender
     */
    final private User sender;

    /**
     * The Receiver
     * (For Firebase)
     */
    final private User receiver;

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
}
