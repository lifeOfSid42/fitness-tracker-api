package com.example.fitnesstracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"user", "workoutPlan"})
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Activity name is required")
    @Size(max = 100, message = "Activity name cannot exceed 100 characters")
    private String activityName;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Date and time is required")
    @PastOrPresent(message = "Activity date cannot be in the future")
    private LocalDateTime dateTime;

    @Positive(message = "Duration must be positive")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 1440, message = "Duration cannot exceed 1440 minutes (24 hours)")
    private Integer durationMinutes;

    @Positive(message = "Calories must be positive")
    private Integer caloriesBurned;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "workout_plan_id")
    private WorkoutPlan workoutPlan;

    public enum ActivityType {
        CARDIO, STRENGTH, FLEXIBILITY, SPORTS, OTHER
    }
}

