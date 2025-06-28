package com.example.fitnesstracker.service.impl;

import com.example.fitnesstracker.exception.ResourceNotFoundException;
import com.example.fitnesstracker.mapper.ActivityLogMapper;
import com.example.fitnesstracker.model.ActivityLog;
import com.example.fitnesstracker.repository.ActivityLogRepository;
import com.example.fitnesstracker.request.ActivityLogRequest;
import com.example.fitnesstracker.service.ActivityLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;

    @Autowired
    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository,
                                  ActivityLogMapper activityLogMapper) {
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
    }

    @Override
    public ActivityLog createActivityLogDto(ActivityLogRequest request) {
        log.info("Creating activity log for userId: {}", request.userId());
        ActivityLog savedLog = activityLogRepository.save(activityLogMapper.toEntityWithResolvedRelationships(request));
        log.debug("Activity log created with ID: {}", savedLog.getId());
        return savedLog;
    }

    @Override
    public ActivityLog updateActivityLogDto(Long id, ActivityLogRequest request) {
        log.info("Updating activity log with ID: {}", id);
        ActivityLog updated = activityLogMapper.updateEntityFromRequest(getActivityLogById(id), request);
        ActivityLog saved = activityLogRepository.save(updated);
        log.debug("Activity log updated: {}", saved);
        return saved;
    }

    @Override
    public ActivityLog getActivityLogById(Long id) {
        log.info("Fetching activity log with ID: {}", id);
        return activityLogRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Activity log not found for ID: {}", id);
                    return new ResourceNotFoundException("ActivityLog", "id", id);
                });
    }

    @Override
    public List<ActivityLog> getAllActivityLogs() {
        log.info("Fetching all activity logs");
        List<ActivityLog> logs = activityLogRepository.findAll();
        log.debug("Total activity logs found: {}", logs.size());
        return logs;
    }

    @Override
    public void deleteActivityLog(Long id) {
        log.info("Deleting activity log with ID: {}", id);
        ActivityLog logEntity = getActivityLogById(id);
        activityLogRepository.delete(logEntity);
        log.debug("Deleted activity log with ID: {}", id);
    }
}