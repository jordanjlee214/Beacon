package com.example.beacon;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

/**
 * This entity stores information on pings that will be used to send notifications and
 * view information about pinged users and what they are doing.
 */
public class PingInfo {
    private String title; //the main title of your "ping," concisely describes what you are doing. ex) "Study Sesh"

    private String description; //additional description of what the user is doing
    private String pingLocation; //specify the specific location on Wheaton
    private String pingedUserID; //the id of the user who sent the ping
    private String pingedUsername; //the username of the pinged user
    private String pingedDisplayName; //the first and last name of the pinged user
    private String endTime; //the specified time the user will be done, if specified

    public PingInfo(String pingedUserID){
        this.pingedUserID = pingedUserID;
    }

    public PingInfo(String title, String description, String pingLocation, String endTime, String pingedUserID, String pingedUsername, String pingedDisplayName){
        this.title = title.trim();
        this.description = description.trim();
        this.pingLocation = pingLocation.trim();
        this.endTime = endTime.trim();
        this.pingedUserID = pingedUserID.trim();
        this.pingedUsername = pingedUsername.trim();
        this.pingedDisplayName = pingedDisplayName.trim();
    }

    public String toString(){
        String message = "";
        if(!endTime.isEmpty() && isLocationSpecific()){
            message = pingedDisplayName + " is pinged at " + pingLocation + " until " + getTime()
                    + ". " + "This is what's happening: " + title;
        }
        else if(endTime.isEmpty() && isLocationSpecific()){
            message = pingedDisplayName + " is pinged at " + pingLocation + ". This is what's happening: " + title;
        }
        else if (!endTime.isEmpty() && !isLocationSpecific()) {
            message = pingedDisplayName + " is pinged until " + getTime()
                    + ". " + "This is what's happening: " + title;
        }
        else if(endTime.isEmpty() && !isLocationSpecific()){
            message = pingedDisplayName + " is pinged! This is what's happening: " + title;
        }

        if(!description.isEmpty()){
            message += "- \"" + description + "\"";
        }

        return message + " | See where they are on the map!";
    }

    public String offPingMessage(){ //this is a message for when the ping is turned off
        String message = "";
        if(isLocationSpecific()){
            message = pingedDisplayName + " is no longer pinged at " + pingLocation + ".";
        }
        else{
            message = pingedDisplayName + " is no longer pinged.";
        }
        return message;
    }


    public HashMap<String, Object> toMap(){
        HashMap<String, Object> toReturn = new HashMap<>();
        toReturn.put("pingTitle", title);
        toReturn.put("pingDescription", description);
        toReturn.put("pingLocation", pingLocation);
        toReturn.put("pingEndTime", endTime);
        toReturn.put("pingedID", pingedUserID);
        toReturn.put("pingedUsername", pingedUsername);
        toReturn.put("pingedDisplayName", pingedDisplayName);
        toReturn.put("pingMessage", toString());
        toReturn.put("offPingMessage", offPingMessage());
        return toReturn;
    }

    public void buildPingFromSnapshot(DataSnapshot snapshot, String id){
        setTitle(snapshot.child(id).child("pingTitle").getValue().toString());
        setDescription(snapshot.child(id).child("pingDescription").getValue().toString());
        setPingLocation(snapshot.child(id).child("pingLocation").getValue().toString());
        setEndTime(snapshot.child(id).child("pingEndTime").getValue().toString());
        setPingedUsername(snapshot.child(id).child("pingedUsername").getValue().toString());
        setPingedDisplayName(snapshot.child(id).child("pingedDisplayName").getValue().toString());
        pingedUserID = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPingedUserID() {
        return pingedUserID;
    }

    public void setPingedUserID(String pingedUserID) {
        this.pingedUserID = pingedUserID;
    }

    public String getPingLocation() {
        return pingLocation;
    }

    public void setPingLocation(String pingLocation) {
        this.pingLocation = pingLocation;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPingedUsername() {
        return pingedUsername;
    }

    public void setPingedUsername(String pingedUsername) {
        this.pingedUsername = pingedUsername;
    }

    public String getPingedDisplayName() {
        return pingedDisplayName;
    }

    public void setPingedDisplayName(String pingedDisplayName) {
        this.pingedDisplayName = pingedDisplayName;
    }


    public String getTime(){ //formats the time to AM/PM
        String timeString = "";
        int hour = Integer.parseInt(endTime.substring(0, endTime.indexOf(":")));
        String minute = endTime.substring(endTime.indexOf(":") + 1);
        if(hour == 0){
            timeString = 12 + ":" + minute + " AM";
        }
        else if(hour > 0 && hour < 12){
            timeString = hour + ":" + minute + " AM";
        }
        else if(hour == 12){
            timeString = hour + ":" + minute + " PM";
        }
        else{
            assert hour > 12;
            timeString = (hour - 12) + ":" + minute + " PM";
        }
        return timeString;
    }

    public boolean isLocationSpecific(){ //is the location specific ?
        return !pingLocation.equals("Other");
    }


}
