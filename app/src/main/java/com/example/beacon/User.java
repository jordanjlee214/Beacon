package com.example.beacon;

import java.util.HashMap;
import java.util.Map;

public class User { //entity class to represent user. Needs to be updated eventually.
    private String username, firstName, lastName, birthday, gender, major, graduationYear;
    private String userID;

    private double lati, longi;

    private int friends; //number of friends

    private HashMap<String, String> friendsList; //stores friends usernames and IDs
    //Profile images
    //Group info
    //Classes, etc.

    public User(){
        username = "";
        firstName = "";
        lastName = "";
        birthday = "";
        gender = "";
        major = "";
        graduationYear= "";
        userID = "";
        friends= 0;
        friendsList = new HashMap<>();
    }

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public HashMap<String, String> getFriendsList() {
        return friendsList;
    }

    public int getFriends() {
        return friends;
    }

    public void setFriendsList(HashMap<String, String> fList) {
        this.friendsList = fList;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setFriends(int friends) {
        this.friends = friends;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGraduationYear(String gradYear) {
        this.graduationYear = gradYear;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Map<String, Object> toMap(){
        HashMap userData = new HashMap();
        userData.put("userID", userID);
        userData.put("username", username);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("gender", gender);
        userData.put("birthday", birthday);
        userData.put("graduationYear", graduationYear);
        userData.put("major", major);
        userData.put("friends", friends);
        userData.put("friendsList", friendsList);
        userData.put("lat", lati);
        userData.put("long", longi);
        return userData;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
}
