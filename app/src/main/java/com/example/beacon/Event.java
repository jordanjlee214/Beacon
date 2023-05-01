package com.example.beacon;
import java.time.*;
import java.util.HashMap;

public class Event {

    private String eventName, eventDescription, eventLocation;

    private Boolean isPublic;

    private String eventDate;

    private String eventStartTime, eventEndTime;

    private String creatorID;
    private String creatorUsername;
    private HashMap<String, String> rsvpList;

    String newline = System.lineSeparator();


//check main activity set up user data, and display user data

    public Event(){
    }

    public Event(String eventName, Boolean isPublic, String eventDate, String startTime, String endTime, String eventLocation, String eventDescription, String creatorID, String creatorUsername) {
        this.eventName = eventName;
        this.isPublic = isPublic;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
        this.eventStartTime = startTime;
        this.eventEndTime = endTime;
        this.eventLocation = eventLocation;
        this.creatorID = creatorID;
        this.creatorUsername = creatorUsername;
    }
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public String toString(){
        return  eventName + newline +
                privacyStatus(isPublic)+ newline +
                "WHEN: " + eventDate + newline +
                "\t\t" + getTime(eventStartTime) + " to " + getTime(eventEndTime) + newline +
                "WHERE: " + eventLocation + newline +
                "WHAT: " + eventDescription + newline +
                "HOST: " + creatorUsername;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public HashMap<String, Object> toMap(){
        HashMap eventData = new HashMap();
        eventData.put("eventName", eventName);
        eventData.put("eventDescription", eventDescription);
        eventData.put("eventLocation", eventLocation);
        eventData.put("creatorID", creatorID);
        eventData.put("creatorUsername", creatorUsername);
        eventData.put("eventDate", eventDate.toString());
        eventData.put("eventStartTime", eventStartTime.toString());
        eventData.put("eventEndTime", eventEndTime.toString());
        eventData.put("rsvpList", rsvpList);
        return eventData;
    }

    public String getTime(String time){ //formats the time to AM/PM
        String timeString = "";
        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        String minute = time.substring(time.indexOf(":") + 1);
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

    public String privacyStatus(boolean isPublic){
        if(isPublic){
            return "PUBLIC";
        }
        else{
            return "PRIVATE";
        }
    }

}
