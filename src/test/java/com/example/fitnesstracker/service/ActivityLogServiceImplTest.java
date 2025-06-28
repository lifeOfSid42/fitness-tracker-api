package com.example.fitnesstracker.service;

import com.example.fitnesstracker.mapper.ActivityLogMapper;
import com.example.fitnesstracker.model.ActivityLog;
import com.example.fitnesstracker.model.User;
import com.example.fitnesstracker.repository.ActivityLogRepository;
import com.example.fitnesstracker.exception.ResourceNotFoundException;

import com.example.fitnesstracker.request.ActivityLogRequest;
import com.example.fitnesstracker.service.impl.ActivityLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ActivityLogServiceImplTest {

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private ActivityLogMapper activityLogMapper;

    @InjectMocks
    private ActivityLogServiceImpl activityLogService;

    private ActivityLogRequest request;
    private ActivityLog activityLog;

    @BeforeEach
    void setUp() {
        request = new ActivityLogRequest(
                "Running",
                "Morning jog",
                LocalDateTime.now(),
                30,
                250,
                ActivityLog.ActivityType.CARDIO,
                1L,
                null
        );

        activityLog = new ActivityLog();
        activityLog.setId(1L);
        activityLog.setActivityName("Running");
        activityLog.setDescription("Morning jog");
        activityLog.setDateTime(request.dateTime());
        activityLog.setDurationMinutes(30);
        activityLog.setCaloriesBurned(250);
        activityLog.setActivityType(ActivityLog.ActivityType.CARDIO);

        User user = new User();
        user.setId(1L);
        activityLog.setUser(user);
    }

    @Test
    void testCreateActivityLogDto() {
        when(activityLogMapper.toEntityWithResolvedRelationships(request)).thenReturn(activityLog);
        when(activityLogRepository.save(activityLog)).thenReturn(activityLog);

        ActivityLog result = activityLogService.createActivityLogDto(request);

        assertNotNull(result);
        assertEquals(activityLog.getActivityName(), result.getActivityName());
        verify(activityLogRepository, times(1)).save(activityLog);
    }

    @Test
    void testUpdateActivityLogDto() {
        ActivityLog updated = new ActivityLog();
        updated.setId(1L);
        updated.setActivityName("Updated Run");

        when(activityLogRepository.findById(1L)).thenReturn(Optional.of(activityLog));
        when(activityLogMapper.updateEntityFromRequest(activityLog, request)).thenReturn(updated);
        when(activityLogRepository.save(updated)).thenReturn(updated);

        ActivityLog result = activityLogService.updateActivityLogDto(1L, request);

        assertEquals("Updated Run", result.getActivityName());
        verify(activityLogRepository).save(updated);
    }

    @Test
    void testGetActivityLogById_found() {
        when(activityLogRepository.findById(1L)).thenReturn(Optional.of(activityLog));
        ActivityLog result = activityLogService.getActivityLogById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetActivityLogById_notFound() {
        when(activityLogRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> activityLogService.getActivityLogById(1L));
    }

    @Test
    void testGetAllActivityLogs() {
        List<ActivityLog> logs = List.of(activityLog);
        when(activityLogRepository.findAll()).thenReturn(logs);

        List<ActivityLog> result = activityLogService.getAllActivityLogs();

        assertEquals(1, result.size());
        verify(activityLogRepository).findAll();
    }

    @Test
    void testDeleteActivityLog() {
        when(activityLogRepository.findById(1L)).thenReturn(Optional.of(activityLog));
        activityLogService.deleteActivityLog(1L);
        verify(activityLogRepository).delete(activityLog);
    }
}
