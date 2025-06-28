package com.example.fitnesstracker.security;

import com.example.fitnesstracker.model.ActivityLog;
import com.example.fitnesstracker.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogSecurity {

    private final ActivityLogService activityLogService;

    @Autowired
    public ActivityLogSecurity(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    public boolean isOwner(Long activityLogId) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            return false;
        }

        try {
            ActivityLog activityLog = activityLogService.getActivityLogById(activityLogId);
            return activityLog.getUser().getUsername().equals(currentUsername);
        } catch (Exception e) {
            return false;
        }
    }
}
