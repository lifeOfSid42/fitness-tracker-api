package com.example.fitnesstracker.controller;

import com.example.fitnesstracker.mapper.ActivityLogMapper;
import com.example.fitnesstracker.model.ActivityLog;
import com.example.fitnesstracker.model.User;
import com.example.fitnesstracker.request.ActivityLogRequest;
import com.example.fitnesstracker.response.ActivityLogResponse;
import com.example.fitnesstracker.response.UserResponse;
import com.example.fitnesstracker.security.UserSecurity;
import com.example.fitnesstracker.service.ActivityLogService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityLogController.class)
@AutoConfigureMockMvc
class ActivityLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityLogService activityLogService;

    @MockBean
    private ActivityLogMapper activityLogMapper;

    @MockBean
    private UserSecurity userSecurity;

    private ActivityLogRequest request;
    private ActivityLog activityLog;
    private ActivityLogResponse response;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        request = new ActivityLogRequest(
                "Running",
                "Morning jog",
                LocalDateTime.of(2024, 1, 15, 6, 30),
                30,
                250,
                ActivityLog.ActivityType.CARDIO,
                1L,
                null
        );

        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        activityLog = new ActivityLog();
        activityLog.setId(1L);
        activityLog.setActivityName("Running");
        activityLog.setDescription("Morning jog");
        activityLog.setDateTime(request.dateTime());
        activityLog.setDurationMinutes(30);
        activityLog.setCaloriesBurned(250);
        activityLog.setActivityType(ActivityLog.ActivityType.CARDIO);
        activityLog.setUser(user);

        UserResponse userResponse = new UserResponse(
                1L,
                "testUser",
                "test@example.com",
                "Test User",
                User.Role.USER,
                LocalDateTime.now()
        );

        response = new ActivityLogResponse(
                1L,
                "Running",
                "Morning jog",
                request.dateTime(),
                30,
                250,
                ActivityLog.ActivityType.CARDIO,
                userResponse,
                null
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllActivityLogs_shouldReturnList() throws Exception {
        when(activityLogService.getAllActivityLogs()).thenReturn(List.of(activityLog));
        when(activityLogMapper.toResponse(activityLog)).thenReturn(response);

        mockMvc.perform(get("/api/activity-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].activityName").value("Running"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getActivityLogById_shouldReturnLog() throws Exception {
        when(activityLogService.getActivityLogById(1L)).thenReturn(activityLog);
        when(activityLogMapper.toResponse(activityLog)).thenReturn(response);

        mockMvc.perform(get("/api/activity-logs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activityName").value("Running"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void createActivityLog_shouldReturnCreated() throws Exception {
        when(userSecurity.isCurrentUser(1L)).thenReturn(true);
        when(activityLogService.createActivityLogDto(any(ActivityLogRequest.class))).thenReturn(activityLog);
        when(activityLogMapper.toResponse(activityLog)).thenReturn(response);

        mockMvc.perform(post("/api/activity-logs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.activityName").value("Running"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void updateActivityLog_shouldReturnUpdated() throws Exception {
        when(userSecurity.isCurrentUser(1L)).thenReturn(true);
        when(activityLogService.updateActivityLogDto(eq(1L), any(ActivityLogRequest.class))).thenReturn(activityLog);
        when(activityLogMapper.toResponse(activityLog)).thenReturn(response);

        mockMvc.perform(put("/api/activity-logs/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activityName").value("Running"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteActivityLog_shouldReturnNoContent() throws Exception {
        doNothing().when(activityLogService).deleteActivityLog(1L);

        mockMvc.perform(delete("/api/activity-logs/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(activityLogService).deleteActivityLog(1L);
    }

    @Test
    @WithMockUser(username = "unauthorizedUser", roles = "USER")
    void createActivityLog_shouldReturnForbidden_whenNotOwnerOrAdmin() throws Exception {
        when(userSecurity.isCurrentUser(1L)).thenReturn(false);

        mockMvc.perform(post("/api/activity-logs")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied: You are not allowed to create activity log for user ID 1"));
    }
}
