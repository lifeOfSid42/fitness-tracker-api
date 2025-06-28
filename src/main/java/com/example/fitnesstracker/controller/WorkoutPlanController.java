package com.example.fitnesstracker.controller;

import com.example.fitnesstracker.model.WorkoutPlan.DifficultyLevel;
import com.example.fitnesstracker.request.WorkoutPlanRequest;
import com.example.fitnesstracker.response.WorkoutPlanResponse;
import com.example.fitnesstracker.service.WorkoutPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-plans")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    @Autowired
    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    @GetMapping
    public ResponseEntity<List<WorkoutPlanResponse>> getAllWorkoutPlans() {
        return ResponseEntity.ok(workoutPlanService.getAllWorkoutPlansDto());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @workoutPlanSecurity.isOwner(#id)")
    public ResponseEntity<WorkoutPlanResponse> getWorkoutPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(workoutPlanService.getWorkoutPlanByIdDto(id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<List<WorkoutPlanResponse>> getPlansByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(workoutPlanService.getWorkoutPlansByUserIdDto(userId));
    }

    @GetMapping("/user/{userId}/difficulty/{difficultyLevel}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<List<WorkoutPlanResponse>> getPlansByUserAndDifficulty(
            @PathVariable Long userId,
            @PathVariable DifficultyLevel difficultyLevel) {
        return ResponseEntity.ok(workoutPlanService.getWorkoutPlansByUserIdAndDifficultyLevelDto(userId, difficultyLevel));
    }

    @GetMapping("/search")
    public ResponseEntity<List<WorkoutPlanResponse>> searchWorkoutPlans(@RequestParam String name) {
        List<WorkoutPlanResponse> results = workoutPlanService.searchWorkoutPlansByNameDto(name);
        return ResponseEntity.ok(results);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WorkoutPlanResponse> createWorkoutPlan(
            @Valid @RequestBody WorkoutPlanRequest workoutPlanRequest) {
        return new ResponseEntity<>(workoutPlanService.createWorkoutPlanDto(workoutPlanRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @workoutPlanSecurity.isOwner(#id)")
    public ResponseEntity<WorkoutPlanResponse> updateWorkoutPlan(
            @PathVariable Long id,
            @Valid @RequestBody WorkoutPlanRequest workoutPlanRequest) {
        return ResponseEntity.ok(workoutPlanService.updateWorkoutPlanDto(id, workoutPlanRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @workoutPlanSecurity.isOwner(#id)")
    public ResponseEntity<Void> deleteWorkoutPlan(@PathVariable Long id) {
        workoutPlanService.deleteWorkoutPlan(id);
        return ResponseEntity.noContent().build();
    }
}