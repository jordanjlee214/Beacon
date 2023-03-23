package com.example.beacon;

import java.util.HashMap;
import java.util.Map;

public class User { //entity class to represent user. Needs to be updated eventually.
    private String username, firstName, lastName, birthday, gender, major, graduationYear;
    private String userID;
    private String photoURL; //url of the Google profile pic of the user

    private String nickname;

    private double lati, longi;

    private int friends; //number of friends

    private HashMap<String, String> friendList, blocked; //stores friends usernames and IDs

    private HashMap<String, FriendRequest> requestList;

    //Profile images
    //Group info
    //Classes, etc.

    public User(){
        username = "";
        firstName = "";
        lastName = "";
        birthday = "";
        nickname = "";
        gender = "";
        major = "";
        graduationYear= "";
        photoURL = "https://firebasestorage.googleapis.com/v0/b/beacon-a071e.appspot.com/o/default.jpeg?alt=media&token=8ce30ccd-2856-48c9-a07b-49572c4bc213";
        userID = "";
        friends= 0;
        friendList = new HashMap<String, String>();
        blocked = new HashMap<String, String>();
        requestList = new HashMap<String, FriendRequest>();
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

    public HashMap<String, String> getFriendList() {
        return friendList;
    }

    public int getFriends() {
        return friends;
    }

    public void setFriendList(HashMap<String, String> fList) {
        this.friendList = fList;
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

    public void setBlocked(HashMap<String, String> blockedM) {
        blocked = blockedM;
    }

    public HashMap<String, String> getBlocked(){
        return blocked;
    }

    public Map<String, Object> toMap(){
        HashMap userData = new HashMap();
        userData.put("userID", userID);
        userData.put("username", username);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("gender", gender);
        userData.put("nickname", nickname);
        userData.put("birthday", birthday);
        userData.put("photoURL", photoURL);
        userData.put("graduationYear", graduationYear);
        userData.put("major", major);
        userData.put("friends", friends);
        userData.put("friendList", friendList);
        userData.put("lat", lati);
        userData.put("long", longi);
        userData.put("blocked", blocked);
        userData.put("requestList", requestList);
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public HashMap<String, FriendRequest> getRequestList() {return requestList;}
}
