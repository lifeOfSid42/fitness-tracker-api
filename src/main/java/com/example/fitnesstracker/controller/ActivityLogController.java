package com.example.fitnesstracker.controller;

import com.example.fitnesstracker.mapper.ActivityLogMapper;
import com.example.fitnesstracker.model.ActivityLog;
import com.example.fitnesstracker.request.ActivityLogRequest;
import com.example.fitnesstracker.response.ActivityLogResponse;
import com.example.fitnesstracker.service.ActivityLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final ActivityLogMapper activityLogMapper;

    @Autowired
    public ActivityLogController(ActivityLogService activityLogService, ActivityLogMapper activityLogMapper) {
        this.activityLogService = activityLogService;
        this.activityLogMapper = activityLogMapper;
    }

    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#request.userId())")
    @PostMapping
    public ResponseEntity<ActivityLogResponse> createActivityLog(@Valid @RequestBody ActivityLogRequest request) {
        ActivityLog created = activityLogService.createActivityLogDto(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityLogMapper.toResponse(created));
    }

    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#request.userId())")
    @PutMapping("/{id}")
    public ResponseEntity<ActivityLogResponse> updateActivityLog(
            @PathVariable Long id,
            @Valid @RequestBody ActivityLogRequest request) {
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
}

