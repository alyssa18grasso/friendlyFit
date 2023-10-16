package com.example.friendlyfit;

import java.util.UUID;

public class Utils {
    public static String generateUniqueWorkoutId(String userEmail, String workoutName) {
        String emailWorkout = userEmail + workoutName;
        return UUID.nameUUIDFromBytes(emailWorkout.getBytes())
                .toString().substring(0, 16);
    }
}
