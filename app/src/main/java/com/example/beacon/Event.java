package com.example.beacon;
import java.time.*;
import java.util.*;

public class Event {


    private String eventName, eventDescription, eventLocation;

    private Boolean isPublic;

    private LocalDate eventDate;

    private LocalTime eventStartTime, eventEndTime;


//check main activity set up user data, and display user data


    public Event(String eventName, Boolean isPublic, LocalDate eventDate, LocalTime startTime, LocalTime endTime, String eventLocation, String eventDescription) {
        this.eventName = eventName;
        this.isPublic = isPublic;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
        this.eventStartTime = startTime;
        this.eventEndTime = endTime;
        this.eventLocation = eventLocation;
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

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(LocalTime eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public LocalTime getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(LocalTime eventEndTime) {
        this.eventEndTime = eventEndTime;
    }



}
