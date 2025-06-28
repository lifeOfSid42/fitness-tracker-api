package com.example.fitnesstracker.mapper;

import com.example.fitnesstracker.model.User;
import com.example.fitnesstracker.model.WorkoutPlan;
import com.example.fitnesstracker.request.WorkoutPlanRequest;
import com.example.fitnesstracker.response.WorkoutPlanResponse;
import com.example.fitnesstracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between WorkoutPlan entity and its DTO representations.
 */
@Component
public class WorkoutPlanMapper {

    private final UserMapper userMapper;
    private final UserService userService;

    @Autowired
    public WorkoutPlanMapper(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    /**
     * Converts a WorkoutPlanRequest DTO to a WorkoutPlan entity.
     *
     * @param workoutPlanRequest the WorkoutPlanRequest DTO
     * @return the WorkoutPlan entity
     */
    public WorkoutPlan toEntity(WorkoutPlanRequest workoutPlanRequest) {
        if (workoutPlanRequest == null) {
            return null;
        }

        WorkoutPlan workoutPlan = new WorkoutPlan();
        workoutPlan.setName(workoutPlanRequest.name());
        workoutPlan.setDescription(workoutPlanRequest.description());
        workoutPlan.setStartDate(workoutPlanRequest.startDate());
        workoutPlan.setEndDate(workoutPlanRequest.endDate());
        workoutPlan.setDifficultyLevel(workoutPlanRequest.difficultyLevel());

        if (workoutPlanRequest.userId() != null) {
            User user = new User();
            user.setId(workoutPlanRequest.userId());
            workoutPlan.setCreatedBy(user);
        }

        return workoutPlan;
    }

    /**
     * Converts a WorkoutPlan entity to a WorkoutPlanResponse DTO.
     *
     * @param workoutPlan the WorkoutPlan entity
     * @return the WorkoutPlanResponse DTO
     */
    public WorkoutPlanResponse toResponse(WorkoutPlan workoutPlan) {
        if (workoutPlan == null) {
            return null;
        }

        return new WorkoutPlanResponse(
                workoutPlan.getId(),
                workoutPlan.getName(),
                workoutPlan.getDescription(),
                workoutPlan.getStartDate(),
                workoutPlan.getEndDate(),
                workoutPlan.getDifficultyLevel(),
                userMapper.toResponse(workoutPlan.getCreatedBy())
        );
    }

    /**
     * Updates an existing WorkoutPlan entity with data from a WorkoutPlanRequest DTO.
     *
     * @param workoutPlan        the existing WorkoutPlan entity
     * @param workoutPlanRequest the WorkoutPlanRequest DTO with updated data
     * @return the updated WorkoutPlan entity
     */
    public WorkoutPlan updateEntityFromRequest(WorkoutPlan workoutPlan, WorkoutPlanRequest workoutPlanRequest) {
        if (workoutPlan == null || workoutPlanRequest == null) {
            return workoutPlan;
        }

        workoutPlan.setName(workoutPlanRequest.name());
        workoutPlan.setDescription(workoutPlanRequest.description());
        workoutPlan.setStartDate(workoutPlanRequest.startDate());
        workoutPlan.setEndDate(workoutPlanRequest.endDate());
        workoutPlan.setDifficultyLevel(workoutPlanRequest.difficultyLevel());
        return workoutPlan;
    }

    /**
     * Creates a WorkoutPlan entity from a WorkoutPlanRequest DTO and resolves the user relationship.
     *
     * @param workoutPlanRequest the WorkoutPlanRequest DTO
     * @return the WorkoutPlan entity with resolved relationships
     */
    public WorkoutPlan toEntityWithResolvedRelationships(WorkoutPlanRequest workoutPlanRequest) {
        WorkoutPlan workoutPlan = toEntity(workoutPlanRequest);

        if (workoutPlan != null && workoutPlanRequest.userId() != null) {
            User user = userService.getUserById(workoutPlanRequest.userId());
            workoutPlan.setCreatedBy(user);
        }

        return workoutPlan;
    }
}