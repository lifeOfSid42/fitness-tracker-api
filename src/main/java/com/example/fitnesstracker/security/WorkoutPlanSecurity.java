package com.example.fitnesstracker.security;

import com.example.fitnesstracker.model.WorkoutPlan;
import com.example.fitnesstracker.service.WorkoutPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkoutPlanSecurity {

    private final WorkoutPlanService workoutPlanService;

    @Autowired
    public WorkoutPlanSecurity(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    public boolean isOwner(Long workoutPlanId) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            return false;
        }

        try {
            WorkoutPlan workoutPlan = workoutPlanService.getWorkoutPlanById(workoutPlanId);
            return workoutPlan.getCreatedBy().getUsername().equals(currentUsername);
        } catch (Exception e) {
            return false;
        }
    }
}

