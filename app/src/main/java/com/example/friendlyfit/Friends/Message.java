package com.example.friendlyfit.Friends;

import com.example.friendlyfit.User;

import java.io.Serializable;

public class Message implements Serializable {

    private User sender;
    private String text;
    private long timestamp;
    private String imageURI;

    public Message(){}

    public Message(User sender, String text, long timestamp, String imageURI) {
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
        this.imageURI = imageURI;
    }

    public Message(String text, User sender, long timestamp) {
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", imageuri='" + imageURI + '\'' +
                '}';
    }
}
