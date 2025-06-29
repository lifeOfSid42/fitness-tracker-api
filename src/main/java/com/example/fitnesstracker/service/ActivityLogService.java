package com.example.fitnesstracker.service;

import com.example.fitnesstracker.model.ActivityLog;
import com.example.fitnesstracker.request.ActivityLogRequest;

import java.util.List;

public interface ActivityLogService {

    ActivityLog createActivityLogDto(ActivityLogRequest request);

    ActivityLog updateActivityLogDto(Long id, ActivityLogRequest request);

    ActivityLog getActivityLogById(Long id);

    List<ActivityLog> getAllActivityLogs();

    void deleteActivityLog(Long id);
}