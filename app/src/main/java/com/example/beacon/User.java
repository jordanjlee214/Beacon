package com.example.beacon;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class User { //entity class to represent user. Needs to be updated eventually.
    private String username, firstName, lastName, birthday, gender, major, graduationYear;
    private String userID;
    private String photoURL; //url of the Google profile pic of the user

    private String nickname;

    private double lati, lngi;

    private boolean ping;

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
        ping = false;
        friends= 0;
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

    public boolean getPing(){
        return ping;
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

    public void setPing(boolean p){
        this.ping = p;
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
        userData.put("lat", lati);
        userData.put("lng", lngi);
        userData.put("ping", ping);
        userData.put("blocked", blocked);
        return userData;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public double getLngi() {
        return lngi;
    }

    public void setLngi(double lngi) {
        this.lngi = lngi;
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

    //CURRENTLY NOT WORKING
    //takes a data snapshot accessing the Users branch and an ID and fills info accordingly
    public void buildUserFromSnapshot(DataSnapshot snapshot, String id){
        setUsername(snapshot.child(id).child("username").getValue().toString());
        setNickname(snapshot.child(id).child("nickname").getValue().toString());
        setFirstName(snapshot.child(id).child("firstName").getValue().toString());
        setLastName(snapshot.child(id).child("lastName").getValue().toString());
        setPhotoURL(snapshot.child(id).child("photoURL").getValue().toString());
        setMajor(snapshot.child(id).child("major").getValue().toString());
        setBirthday(snapshot.child(id).child("birthday").getValue().toString());
        setGraduationYear(snapshot.child(id).child("graduationYear").getValue().toString());
        setGender(snapshot.child(id).child("gender").getValue().toString());
        setFriends(Integer.parseInt(snapshot.child(id).child("friends").getValue().toString()));
        setLati(Double.parseDouble(snapshot.child(id).child("lat").getValue().toString()));
        setLngi(Double.parseDouble(snapshot.child(id).child("lng").getValue().toString()));
        setPing((Boolean) snapshot.child(id).child("ping").getValue());
        setUserID(id);
    }

    public static User buildUserFromFriendSnapshot(DataSnapshot snapshot, String id){
        User newUser = new User();
        newUser.setUsername(snapshot.getValue().toString());
        newUser.setUserID(id);
        return newUser;
    }
}
