package com.example.fitnesstracker.request;

import com.example.fitnesstracker.model.ActivityLog;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record ActivityLogRequest(
        @NotBlank String activityName,
        String description,
        @PastOrPresent LocalDateTime dateTime,
        @Min(1) @Max(1440) Integer durationMinutes,
        @Positive Integer caloriesBurned,
        ActivityLog.ActivityType activityType,
        Long userId,
        Long workoutPlanId
) {
}

