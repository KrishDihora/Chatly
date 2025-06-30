package com.krizyo.chatly.Model;

import com.google.firebase.Timestamp;

public class User {
    private String mobileNumber;
    private String username;
    private Timestamp createdTimestamp;
    private String userID;
    private String FCMToken;

    public User() {
    }

    public User(String mobileNumber, String username, Timestamp createdTimestamp, String userID) {
        this.mobileNumber = mobileNumber;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userID = userID;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }
}
