package com.example.friendlyfit;

import android.net.Uri;
import android.widget.ProgressBar;

public interface IDisplayPhoto {
    void retakePhoto(User userInfo, String tag);

    void uploadPhoto(Uri imageUri, ProgressBar progressBar, User userInfo, String tag);

    void uploadSuccess(User userInfo, String tag);
}
