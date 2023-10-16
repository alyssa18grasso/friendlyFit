package com.example.friendlyfit;

public interface IEditProfile {
    void backToUserProfile();

    void updateUserProfile(User userInfo);

    void startCamera(User userInfo, String tag);

    void toHomeDashboard();
}
