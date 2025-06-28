package com.example.fitnesstracker.repository;

import com.example.fitnesstracker.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByUserId(Long userId);

    List<ActivityLog> findByWorkoutPlanId(Long workoutPlanId);

    List<ActivityLog> findByUserIdAndActivityType(Long userId, ActivityLog.ActivityType activityType);

    List<ActivityLog> findByDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<ActivityLog> findByUserIdAndDateTimeBetween(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}