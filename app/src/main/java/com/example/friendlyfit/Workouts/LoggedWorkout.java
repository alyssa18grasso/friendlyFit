package com.example.friendlyfit.Workouts;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class LoggedWorkout {
    private String workoutDocumentReferenceId;
    private int timeInMinutes;
    @ServerTimestamp
    private Date date;

    public LoggedWorkout() {
        // required empty constructor
    }

    public LoggedWorkout(String workoutDocumentReferenceId, int timeInMinutes) {
        this.workoutDocumentReferenceId = workoutDocumentReferenceId;
        this.timeInMinutes = timeInMinutes;
    }

    public String getWorkoutDocumentReferenceId() {
        return workoutDocumentReferenceId;
    }

    public int getTimeInMinutes() {
        return timeInMinutes;
    }

    public Date getDate() {
        return date;
    }
}
