package com.example.fitnesstracker.response;

import com.example.fitnesstracker.model.ActivityLog;

import java.time.LocalDateTime;

public record ActivityLogResponse(
        Long id,
        String activityName,
        String description,
        LocalDateTime dateTime,
        Integer durationMinutes,
        Integer caloriesBurned,
        ActivityLog.ActivityType activityType,
        UserResponse user,
        WorkoutPlanResponse workoutPlan
) {
}
