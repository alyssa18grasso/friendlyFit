package com.example.friendlyfit.Workouts;

public interface IHandleWorkouts {
    void logSelectedWorkoutPressed(String workoutId, int timeInMinutes);
    void shareSelectedWorkoutPressed(String workoutId);
    void createNewWorkoutPressed();
}
