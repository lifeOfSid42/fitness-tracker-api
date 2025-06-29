package com.example.fitnesstracker.controller;

import com.example.fitnesstracker.model.User;
import com.example.fitnesstracker.model.WorkoutPlan;
import com.example.fitnesstracker.request.WorkoutPlanRequest;
import com.example.fitnesstracker.response.UserResponse;
import com.example.fitnesstracker.response.WorkoutPlanResponse;
import com.example.fitnesstracker.security.WorkoutPlanSecurity;
import com.example.fitnesstracker.service.WorkoutPlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkoutPlanController.class)
@AutoConfigureMockMvc
class WorkoutPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkoutPlanService workoutPlanService;

    @MockBean
    private WorkoutPlanSecurity workoutPlanSecurity;

    private WorkoutPlanResponse workoutPlanResponse;
    private WorkoutPlanRequest workoutPlanRequest;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        workoutPlanRequest = new WorkoutPlanRequest(
                "Strength Training",
                "Full body routine",
                LocalDate.now(),
                LocalDate.now().plusWeeks(4),
                WorkoutPlan.DifficultyLevel.INTERMEDIATE,
                1L
        );

        UserResponse userResponse = new UserResponse(
                1L,
                "testUser",
                "test@example.com",
                "Test User",
                User.Role.USER,
                LocalDateTime.now()
        );

        workoutPlanResponse = new WorkoutPlanResponse(
                1L,
                "Strength Training",
                "Full body routine",
                LocalDate.now(),
                LocalDate.now().plusWeeks(4),
                WorkoutPlan.DifficultyLevel.INTERMEDIATE,
                userResponse
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllWorkoutPlans_shouldReturnList() throws Exception {
        when(workoutPlanService.getAllWorkoutPlansDto()).thenReturn(List.of(workoutPlanResponse));

        mockMvc.perform(get("/api/workout-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Strength Training"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getWorkoutPlanById_shouldReturnPlan() throws Exception {
        when(workoutPlanSecurity.isOwner(1L)).thenReturn(true);
        when(workoutPlanService.getWorkoutPlanByIdDto(1L)).thenReturn(workoutPlanResponse);

        mockMvc.perform(get("/api/workout-plans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Strength Training"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void createWorkoutPlan_shouldReturnCreated() throws Exception {
        when(workoutPlanService.createWorkoutPlanDto(any())).thenReturn(workoutPlanResponse);

        mockMvc.perform(post("/api/workout-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutPlanRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Strength Training"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateWorkoutPlan_shouldReturnUpdatedPlan() throws Exception {
        when(workoutPlanSecurity.isOwner(1L)).thenReturn(true);
        when(workoutPlanService.updateWorkoutPlanDto(eq(1L), any())).thenReturn(workoutPlanResponse);

        mockMvc.perform(put("/api/workout-plans/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutPlanRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Strength Training"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteWorkoutPlan_shouldReturnNoContent() throws Exception {
        when(workoutPlanSecurity.isOwner(1L)).thenReturn(true);
        doNothing().when(workoutPlanService).deleteWorkoutPlan(1L);

        mockMvc.perform(delete("/api/workout-plans/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(workoutPlanService).deleteWorkoutPlan(1L);
    }
}
