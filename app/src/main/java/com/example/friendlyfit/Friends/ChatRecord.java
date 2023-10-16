package com.example.friendlyfit.Friends;

import java.util.ArrayList;

public class ChatRecord {

    private String chat_name;
    private String documentReference;
    private ArrayList<String> user_emails;

    public ChatRecord(){}

    public ChatRecord(String chat_name,String documentReference, ArrayList<String> user_emails) {
        this.chat_name = chat_name;
        this.documentReference = documentReference;
        this.user_emails = user_emails;
    }

    @Override
    public String toString() {
        return "ChatRecord{" +
                "documentReference='" + documentReference + '\'' +
                ", user_emails=" + user_emails +
                '}';
    }

    public String getChat_name() {
        return chat_name;
    }

    public void setChat_name(String chat_name) {
        this.chat_name = chat_name;
    }

    public String getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(String documentReference) {
        this.documentReference = documentReference;
    }

    public ArrayList<String> getUser_emails() {
        return user_emails;
    }

    public void setUser_emails(ArrayList<String> user_emails) {
        this.user_emails = user_emails;
    }
}
