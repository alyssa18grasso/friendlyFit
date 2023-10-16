package com.example.friendlyfit.Workouts;

import java.util.ArrayList;

public class Workout {
    private String workoutDocumentId;
    private String workoutName;
    private ArrayList<String> exercises;
    private boolean isSelected;
    private String creatorEmail;

    public Workout(String workoutDocumentId, String workoutName, ArrayList<String> exercises, String creatorEmail) {
        this.workoutDocumentId = workoutDocumentId;
        this.workoutName = workoutName;
        this.exercises = exercises;
        this.creatorEmail = creatorEmail;
        this.isSelected = false;
    }

    public String getWorkoutDocumentId() {
        return workoutDocumentId;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public ArrayList<String> getExercises() {
        return exercises;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
