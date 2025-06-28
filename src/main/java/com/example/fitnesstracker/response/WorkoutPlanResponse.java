package com.example.fitnesstracker.response;

import com.example.fitnesstracker.model.WorkoutPlan;

import java.time.LocalDate;

public record WorkoutPlanResponse(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        WorkoutPlan.DifficultyLevel difficultyLevel,
        UserResponse creator
) {
}
