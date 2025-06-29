package com.example.fitnesstracker.service;

import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.mapper.WorkoutPlanMapper;
import com.example.fitnesstracker.model.User;
import com.example.fitnesstracker.model.WorkoutPlan;
import com.example.fitnesstracker.model.WorkoutPlan.DifficultyLevel;
import com.example.fitnesstracker.repository.WorkoutPlanRepository;
import com.example.fitnesstracker.request.WorkoutPlanRequest;
import com.example.fitnesstracker.response.WorkoutPlanResponse;
import com.example.fitnesstracker.service.impl.WorkoutPlanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutPlanServiceImplTest {

    @Mock
    private WorkoutPlanRepository workoutPlanRepository;
    @Mock
    private WorkoutPlanMapper workoutPlanMapper;

    @InjectMocks
    private WorkoutPlanServiceImpl workoutPlanService;

    private WorkoutPlanRequest request;
    private WorkoutPlan workoutPlan;
    private WorkoutPlanResponse response;

    @BeforeEach
    void setUp() {
        request = new WorkoutPlanRequest(
                "Strength Plan",
                "Muscle gain program",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                DifficultyLevel.INTERMEDIATE,
                1L
        );

        workoutPlan = new WorkoutPlan();
        workoutPlan.setId(1L);
        workoutPlan.setName("Strength Plan");
        workoutPlan.setDescription("Muscle gain program");
        workoutPlan.setStartDate(request.startDate());
        workoutPlan.setEndDate(request.endDate());
        workoutPlan.setDifficultyLevel(request.difficultyLevel());

        User user = new User();
        user.setId(1L);
        workoutPlan.setCreatedBy(user);

        response = new WorkoutPlanResponse(
                1L,
                "Strength Plan",
                "Muscle gain program",
                request.startDate(),
                request.endDate(),
                DifficultyLevel.INTERMEDIATE,
                null
        );
    }

    @Test
    void testCreateWorkoutPlanDto() {
        when(workoutPlanMapper.toEntityWithResolvedRelationships(request)).thenReturn(workoutPlan);
        when(workoutPlanRepository.save(workoutPlan)).thenReturn(workoutPlan);
        when(workoutPlanMapper.toResponse(workoutPlan)).thenReturn(response);

        WorkoutPlanResponse result = workoutPlanService.createWorkoutPlanDto(request);

        assertEquals("Strength Plan", result.name());
        verify(workoutPlanRepository).save(workoutPlan);
    }

    @Test
    void testUpdateWorkoutPlanDto() {
        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.of(workoutPlan));
        when(workoutPlanMapper.updateEntityFromRequest(workoutPlan, request)).thenReturn(workoutPlan);
        when(workoutPlanRepository.save(workoutPlan)).thenReturn(workoutPlan);
        when(workoutPlanMapper.toResponse(workoutPlan)).thenReturn(response);

        WorkoutPlanResponse result = workoutPlanService.updateWorkoutPlanDto(1L, request);

        assertEquals("Strength Plan", result.name());
        verify(workoutPlanRepository).save(workoutPlan);
    }

    @Test
    void testGetWorkoutPlanByIdDto() {
        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.of(workoutPlan));
        when(workoutPlanMapper.toResponse(workoutPlan)).thenReturn(response);

        WorkoutPlanResponse result = workoutPlanService.getWorkoutPlanByIdDto(1L);

        assertEquals("Strength Plan", result.name());
    }

    @Test
    void testGetWorkoutPlansByUserIdDto() {
        when(workoutPlanRepository.findByCreatedById(1L)).thenReturn(List.of(workoutPlan));
        when(workoutPlanMapper.toResponse(workoutPlan)).thenReturn(response);

        List<WorkoutPlanResponse> result = workoutPlanService.getWorkoutPlansByUserIdDto(1L);

        assertEquals(1, result.size());
        assertEquals("Strength Plan", result.get(0).name());
    }

    @Test
    void testGetWorkoutPlansByUserIdAndDifficultyLevelDto() {
        when(workoutPlanRepository.findByCreatedByIdAndDifficultyLevel(1L, DifficultyLevel.INTERMEDIATE))
                .thenReturn(List.of(workoutPlan));
        when(workoutPlanMapper.toResponse(workoutPlan)).thenReturn(response);

        List<WorkoutPlanResponse> result =
                workoutPlanService.getWorkoutPlansByUserIdAndDifficultyLevelDto(1L, DifficultyLevel.INTERMEDIATE);

        assertEquals(1, result.size());
        assertEquals(DifficultyLevel.INTERMEDIATE, result.get(0).difficultyLevel());
    }

    @Test
    void testSearchWorkoutPlansByNameDto() {
        when(workoutPlanRepository.findByNameContainingIgnoreCase("strength")).thenReturn(List.of(workoutPlan));
        when(workoutPlanMapper.toResponse(workoutPlan)).thenReturn(response);
        List<WorkoutPlanResponse> result = workoutPlanService.searchWorkoutPlansByNameDto("strength");
        assertEquals(1, result.size());
        assertEquals("Strength Plan", result.get(0).name());
    }

    @Test
    void testGetAllWorkoutPlansDto() {
        when(workoutPlanRepository.findAll()).thenReturn(List.of(workoutPlan));
        when(workoutPlanMapper.toResponse(workoutPlan)).thenReturn(response);
        List<WorkoutPlanResponse> result = workoutPlanService.getAllWorkoutPlansDto();
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteWorkoutPlan() {
        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.of(workoutPlan));
        workoutPlanService.deleteWorkoutPlan(1L);
        verify(workoutPlanRepository).delete(workoutPlan);
    }

    @Test
    void testGetWorkoutPlanById_notFound() {
        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> workoutPlanService.getWorkoutPlanById(1L));
    }
}