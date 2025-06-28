package com.example.fitnesstracker.request;

import com.example.fitnesstracker.model.WorkoutPlan;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record WorkoutPlanRequest(
        @NotBlank String name,
        String description,
        @FutureOrPresent LocalDate startDate,
        @Future LocalDate endDate,
        WorkoutPlan.DifficultyLevel difficultyLevel,
        Long userId
) {
}

