package com.example.fitnesstracker.controller;

import com.example.fitnesstracker.mapper.ActivityLogMapper;
import com.example.fitnesstracker.model.ActivityLog;
import com.example.fitnesstracker.request.ActivityLogRequest;
import com.example.fitnesstracker.response.ActivityLogResponse;
import com.example.fitnesstracker.security.UserSecurity;
import com.example.fitnesstracker.service.ActivityLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final ActivityLogMapper activityLogMapper;
    private final UserSecurity userSecurity;

    @Autowired
    public ActivityLogController(ActivityLogService activityLogService,
                                 ActivityLogMapper activityLogMapper,
                                 UserSecurity userSecurity) {
        this.activityLogService = activityLogService;
        this.activityLogMapper = activityLogMapper;
        this.userSecurity = userSecurity;
    }

    @PostMapping
    public ResponseEntity<ActivityLogResponse> createActivityLog(@Valid @RequestBody ActivityLogRequest request) {
        authorizeUser(request.userId(), "create activity log");
        ActivityLog created = activityLogService.createActivityLogDto(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityLogMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityLogResponse> updateActivityLog(
            @PathVariable Long id,
            @Valid @RequestBody ActivityLogRequest request) {
        authorizeUser(request.userId(), "update activity log");
        ActivityLog updated = activityLogService.updateActivityLogDto(id, request);
        return ResponseEntity.ok(activityLogMapper.toResponse(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ActivityLogResponse> getActivityLogById(@PathVariable Long id) {
        ActivityLog logEntity = activityLogService.getActivityLogById(id);
        return ResponseEntity.ok(activityLogMapper.toResponse(logEntity));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ActivityLogResponse>> getAllActivityLogs() {
        List<ActivityLog> logs = activityLogService.getAllActivityLogs();
        return ResponseEntity.ok(logs.stream()
                .map(activityLogMapper::toResponse)
                .toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivityLog(@PathVariable Long id) {
        activityLogService.deleteActivityLog(id);
        return ResponseEntity.noContent().build();
    }

    private void authorizeUser(Long userId, String action) {
        if (!hasAdminRole() && !userSecurity.isCurrentUser(userId)) {
            throw new AccessDeniedException("Access denied: You are not allowed to " + action + " for user ID " + userId);
        }
    }

    private boolean hasAdminRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}


