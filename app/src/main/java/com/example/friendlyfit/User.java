package com.example.friendlyfit;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class User implements Parcelable {
    private String email;
    private String username;
    private String profilePictureUri;
    private String favoriteExercise;
    private int goalNumber;
    private String goalNumerator;
    private String goalDenominator;
    private String bio;

    public User() {
        // required empty constructor
    }

    // These are the two fields that will be filled out when the user registers
    public User(String email, String username) {
        this.email = email;
        this.username = username;
        this.profilePictureUri = "";
        this.favoriteExercise = "";
        this.goalNumber = 0;
        this.goalNumerator = "";
        this.goalDenominator = "";
        this.bio = "";
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePictureUri() {
        return profilePictureUri;
    }

    public String getFavoriteExercise() {
        return favoriteExercise;
    }

    public int getGoalNumber() {
        return goalNumber;
    }

    public String getGoalNumerator() {
        return goalNumerator;
    }

    public String getGoalDenominator() {
        return goalDenominator;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfilePictureUri(String profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }

    public String getBio() {
        return bio;
    }

    public void setFavoriteExercise(String favoriteExercise) {
        this.favoriteExercise = favoriteExercise;
    }

    public void setGoalNumber(int goalNumber) {
        this.goalNumber = goalNumber;
    }

    public void setGoalNumerator(String goalNumerator) {
        this.goalNumerator = goalNumerator;
    }

    public void setGoalDenominator(String goalDenominator) {
        this.goalDenominator = goalDenominator;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    protected User(Parcel in) {
        email = in.readString();
        username = in.readString();
        profilePictureUri = in.readString();
        favoriteExercise = in.readString();
        goalNumber = in.readInt();
        goalNumerator = in.readString();
        goalDenominator = in.readString();
        bio = in.readString();
    }

    @Override
    public String toString() {
        String userString = "User( "
                + "email: " + email + ", "
                + "username: " + username + ", "
                + "profilePictureUri: " + profilePictureUri + ", "
                + "favoriteExercise: " + favoriteExercise + ", "
                + "goalNumber: " + goalNumber + ", "
                + "goalNumerator: " + goalNumerator + ", "
                + "goalDenominator: " + goalDenominator + ", "
                + "bio: " + bio + ")";
        return userString;
    }

    public void update(User userInfo) {
        this.username = userInfo.getUsername();
        this.profilePictureUri = userInfo.getProfilePictureUri();
        this.favoriteExercise = userInfo.getFavoriteExercise();
        this.goalNumber = userInfo.getGoalNumber();
        this.goalNumerator = userInfo.getGoalNumerator();
        this.goalDenominator = userInfo.getGoalDenominator();
        this.bio = userInfo.getBio();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(username);
        parcel.writeString(profilePictureUri);
        parcel.writeString(favoriteExercise);
        parcel.writeInt(goalNumber);
        parcel.writeString(goalNumerator);
        parcel.writeString(goalDenominator);
        parcel.writeString(bio);
    }

}
