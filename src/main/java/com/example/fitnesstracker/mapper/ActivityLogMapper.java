package com.example.fitnesstracker.mapper;

import com.example.fitnesstracker.model.ActivityLog;
import com.example.fitnesstracker.model.User;
import com.example.fitnesstracker.model.WorkoutPlan;
import com.example.fitnesstracker.request.ActivityLogRequest;
import com.example.fitnesstracker.response.ActivityLogResponse;
import com.example.fitnesstracker.service.UserService;
import com.example.fitnesstracker.service.WorkoutPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between ActivityLog entity and its DTO representations.
 */
@Component
public class ActivityLogMapper {

    private final UserMapper userMapper;
    private final WorkoutPlanMapper workoutPlanMapper;
    private final UserService userService;
    private final WorkoutPlanService workoutPlanService;

    @Autowired
    public ActivityLogMapper(UserMapper userMapper,
                             WorkoutPlanMapper workoutPlanMapper,
                             UserService userService,
                             WorkoutPlanService workoutPlanService) {
        this.userMapper = userMapper;
        this.workoutPlanMapper = workoutPlanMapper;
        this.userService = userService;
        this.workoutPlanService = workoutPlanService;
    }

    /**
     * Converts an ActivityLogRequest DTO to an ActivityLog entity.
     *
     * @param activityLogRequest the ActivityLogRequest DTO
     * @return the ActivityLog entity
     */
    public ActivityLog toEntity(ActivityLogRequest activityLogRequest) {
        if (activityLogRequest == null) {
            return null;
        }

        ActivityLog activityLog = new ActivityLog();
        activityLog.setActivityName(activityLogRequest.activityName());
        activityLog.setDescription(activityLogRequest.description());
        activityLog.setDateTime(activityLogRequest.dateTime());
        activityLog.setDurationMinutes(activityLogRequest.durationMinutes());
        activityLog.setCaloriesBurned(activityLogRequest.caloriesBurned());
        activityLog.setActivityType(activityLogRequest.activityType());

        if (activityLogRequest.userId() != null) {
            User user = new User();
            user.setId(activityLogRequest.userId());
            activityLog.setUser(user);
        }

        if (activityLogRequest.workoutPlanId() != null) {
            WorkoutPlan workoutPlan = new WorkoutPlan();
            workoutPlan.setId(activityLogRequest.workoutPlanId());
            activityLog.setWorkoutPlan(workoutPlan);
        }

        return activityLog;
    }

    /**
     * Converts an ActivityLog entity to an ActivityLogResponse DTO.
     *
     * @param activityLog the ActivityLog entity
     * @return the ActivityLogResponse DTO
     */
    public ActivityLogResponse toResponse(ActivityLog activityLog) {
        if (activityLog == null) {
            return null;
        }

        return new ActivityLogResponse(
                activityLog.getId(),
                activityLog.getActivityName(),
                activityLog.getDescription(),
                activityLog.getDateTime(),
                activityLog.getDurationMinutes(),
                activityLog.getCaloriesBurned(),
                activityLog.getActivityType(),
                userMapper.toResponse(activityLog.getUser()),
                activityLog.getWorkoutPlan() != null ? workoutPlanMapper.toResponse(activityLog.getWorkoutPlan()) : null
        );
    }

    /**
     * Updates an existing ActivityLog entity with data from an ActivityLogRequest DTO.
     *
     * @param activityLog        the existing ActivityLog entity
     * @param activityLogRequest the ActivityLogRequest DTO with updated data
     * @return the updated ActivityLog entity
     */
    public ActivityLog updateEntityFromRequest(ActivityLog activityLog, ActivityLogRequest activityLogRequest) {
        if (activityLog == null || activityLogRequest == null) {
            return activityLog;
        }

        activityLog.setActivityName(activityLogRequest.activityName());
        activityLog.setDescription(activityLogRequest.description());
        activityLog.setDateTime(activityLogRequest.dateTime());
        activityLog.setDurationMinutes(activityLogRequest.durationMinutes());
        activityLog.setCaloriesBurned(activityLogRequest.caloriesBurned());
        activityLog.setActivityType(activityLogRequest.activityType());

        if (activityLogRequest.workoutPlanId() != null) {
            WorkoutPlan workoutPlan = new WorkoutPlan();
            workoutPlan.setId(activityLogRequest.workoutPlanId());
            activityLog.setWorkoutPlan(workoutPlan);
        } else {
            activityLog.setWorkoutPlan(null);
        }

        return activityLog;
    }

    /**
     * Creates an ActivityLog entity from an ActivityLogRequest DTO and resolves the relationships.
     *
     * @param activityLogRequest the ActivityLogRequest DTO
     * @return the ActivityLog entity with resolved relationships
     */
    public ActivityLog toEntityWithResolvedRelationships(ActivityLogRequest activityLogRequest) {
        ActivityLog activityLog = toEntity(activityLogRequest);

        if (activityLog != null) {
            if (activityLogRequest.userId() != null) {
                User user = userService.getUserById(activityLogRequest.userId());
                activityLog.setUser(user);
            }

            if (activityLogRequest.workoutPlanId() != null) {
                WorkoutPlan workoutPlan = workoutPlanService.getWorkoutPlanById(activityLogRequest.workoutPlanId());
                activityLog.setWorkoutPlan(workoutPlan);
            }
        }

        return activityLog;
    }
}