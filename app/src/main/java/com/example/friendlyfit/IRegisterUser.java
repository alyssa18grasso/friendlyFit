package com.example.friendlyfit;

import com.google.firebase.auth.FirebaseUser;

public interface IRegisterUser {
    // void loginButtonPressed();
    void registerUser(FirebaseUser currentUser, User user);
    // void uploadProfilePicturePressed();
}
