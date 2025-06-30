package com.krizyo.chatly.Model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private String chatRoomID,lastMessageSenderID;
    private List<String> userIDs;
    private Timestamp lastMessageTimestamp;
    private String lastMessage;

    public ChatRoom() {
    }

    public ChatRoom(String chatRoomID, List<String> userIDs, Timestamp lastMessageTimestamp, String lastMessageSenderID) {
        this.chatRoomID = chatRoomID;
        this.lastMessageSenderID = lastMessageSenderID;
        this.userIDs = userIDs;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getChatRoomID() {
        return chatRoomID;
    }

    public void setChatRoomID(String chatRoomID) {
        this.chatRoomID = chatRoomID;
    }

    public String getLastMessageSenderID() {
        return lastMessageSenderID;
    }

    public void setLastMessageSenderID(String lastMessageSenderID) {
        this.lastMessageSenderID = lastMessageSenderID;
    }

    public List<String> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(List<String> userIDs) {
        this.userIDs = userIDs;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
