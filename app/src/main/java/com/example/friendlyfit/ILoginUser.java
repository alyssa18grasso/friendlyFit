package com.example.friendlyfit;

import com.google.firebase.auth.FirebaseUser;

public interface ILoginUser {
    void loginPressed(FirebaseUser currentUser);
}
