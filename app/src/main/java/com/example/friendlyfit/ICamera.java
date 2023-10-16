package com.example.friendlyfit;

import android.net.Uri;

public interface ICamera {
    void onTakePhoto(Uri imageUri, User userInfo, String tag);
    void backToEditProfile(User userInfo);
    void backToRegisterProfile(User userInfo);
}
