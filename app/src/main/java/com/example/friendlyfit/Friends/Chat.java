package com.example.friendlyfit.Friends;

import java.util.ArrayList;

public class Chat {

    private String chatName;
    private ArrayList<String> emails;
    // messages ???

    public Chat() {

    }
    public Chat(String chatName, ArrayList<String> emails) {
        this.chatName = chatName;
        this.emails = emails;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }
}
