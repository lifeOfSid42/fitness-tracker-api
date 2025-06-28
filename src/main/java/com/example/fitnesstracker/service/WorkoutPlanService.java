package com.example.fitnesstracker.service;

import com.example.fitnesstracker.model.WorkoutPlan;
import com.example.fitnesstracker.model.WorkoutPlan.DifficultyLevel;
import com.example.fitnesstracker.request.WorkoutPlanRequest;
import com.example.fitnesstracker.response.WorkoutPlanResponse;

import java.util.List;

public interface WorkoutPlanService {

    WorkoutPlanResponse createWorkoutPlanDto(WorkoutPlanRequest request);

    WorkoutPlanResponse updateWorkoutPlanDto(Long id, WorkoutPlanRequest request);

    WorkoutPlanResponse getWorkoutPlanByIdDto(Long id);

    List<WorkoutPlanResponse> getWorkoutPlansByUserIdDto(Long userId);

    List<WorkoutPlanResponse> getWorkoutPlansByUserIdAndDifficultyLevelDto(Long userId, DifficultyLevel difficultyLevel);

    List<WorkoutPlanResponse> searchWorkoutPlansByNameDto(String name);

    List<WorkoutPlanResponse> getAllWorkoutPlansDto();

    void deleteWorkoutPlan(Long id);

    WorkoutPlan getWorkoutPlanById(Long id);
}