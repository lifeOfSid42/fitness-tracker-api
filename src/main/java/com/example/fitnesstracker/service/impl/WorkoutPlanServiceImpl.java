package com.example.fitnesstracker.service.impl;

import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.mapper.WorkoutPlanMapper;
import com.example.fitnesstracker.model.WorkoutPlan;
import com.example.fitnesstracker.model.WorkoutPlan.DifficultyLevel;
import com.example.fitnesstracker.repository.WorkoutPlanRepository;
import com.example.fitnesstracker.request.WorkoutPlanRequest;
import com.example.fitnesstracker.response.WorkoutPlanResponse;
import com.example.fitnesstracker.service.WorkoutPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutPlanMapper workoutPlanMapper;

    @Autowired
    public WorkoutPlanServiceImpl(WorkoutPlanRepository workoutPlanRepository,
                                  WorkoutPlanMapper workoutPlanMapper) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutPlanMapper = workoutPlanMapper;
    }

    @Override
    public WorkoutPlanResponse createWorkoutPlanDto(WorkoutPlanRequest request) {
        log.info("Creating workout plan for userId: {}", request.userId());
        WorkoutPlan saved = workoutPlanRepository.save(workoutPlanMapper.toEntityWithResolvedRelationships(request));
        log.debug("Workout plan created with ID: {}", saved.getId());
        return workoutPlanMapper.toResponse(saved);
    }

    @Override
    public WorkoutPlanResponse updateWorkoutPlanDto(Long id, WorkoutPlanRequest request) {
        log.info("Updating workout plan with ID: {}", id);
        WorkoutPlan updated = workoutPlanMapper.updateEntityFromRequest(getWorkoutPlanById(id), request);
        WorkoutPlan saved = workoutPlanRepository.save(updated);
        log.debug("Workout plan updated with ID: {}", saved.getId());
        return workoutPlanMapper.toResponse(saved);
    }

    @Override
    public WorkoutPlanResponse getWorkoutPlanByIdDto(Long id) {
        log.info("Fetching workout plan DTO by ID: {}", id);
        return workoutPlanMapper.toResponse(getWorkoutPlanById(id));
    }

    @Override
    public List<WorkoutPlanResponse> getWorkoutPlansByUserIdDto(Long userId) {
        log.info("Fetching workout plans for userId: {}", userId);
        return workoutPlanRepository.findByCreatedById(userId).stream()
                .map(workoutPlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkoutPlanResponse> getWorkoutPlansByUserIdAndDifficultyLevelDto(Long userId, DifficultyLevel difficultyLevel) {
        log.info("Fetching workout plans for userId: {} with difficulty level: {}", userId, difficultyLevel);
        return workoutPlanRepository.findByCreatedByIdAndDifficultyLevel(userId, difficultyLevel).stream()
                .map(workoutPlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkoutPlanResponse> searchWorkoutPlansByNameDto(String name) {
        log.info("Searching workout plans by name: {}", name);
        return workoutPlanRepository.findByNameContainingIgnoreCase(name).stream()
                .map(workoutPlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkoutPlanResponse> getAllWorkoutPlansDto() {
        log.info("Fetching all workout plans");
        return workoutPlanRepository.findAll().stream()
                .map(workoutPlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteWorkoutPlan(Long id) {
        log.info("Deleting workout plan with ID: {}", id);
        workoutPlanRepository.delete(getWorkoutPlanById(id));
        log.debug("Workout plan deleted with ID: {}", id);
    }

    @Override
    public WorkoutPlan getWorkoutPlanById(Long id) {
        log.debug("Retrieving workout plan by ID: {}", id);
        return workoutPlanRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Workout plan not found with ID: {}", id);
                    return new ResourceNotFoundException("WorkoutPlan", "id", id);
                });
    }
}