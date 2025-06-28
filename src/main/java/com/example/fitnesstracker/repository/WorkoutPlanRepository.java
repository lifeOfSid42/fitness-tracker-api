package com.example.fitnesstracker.repository;

import com.example.fitnesstracker.model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {

    List<WorkoutPlan> findByCreatedById(Long userId);

    List<WorkoutPlan> findByCreatedByIdAndDifficultyLevel(Long userId, WorkoutPlan.DifficultyLevel difficultyLevel);

    List<WorkoutPlan> findByNameContainingIgnoreCase(String name);
}
