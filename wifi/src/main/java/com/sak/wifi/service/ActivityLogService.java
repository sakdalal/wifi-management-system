package com.sak.wifi.service;

import com.sak.wifi.entity.ActivityLog;
import com.sak.wifi.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public void logActivity(String action,
                            String description,
                            String performedBy){

        ActivityLog log= ActivityLog.builder()
                .action(action)
                .description(description)
                .performedBy(performedBy)
                .createdAt(LocalDateTime.now())
                .build();

        activityLogRepository.save(log);

    }
}
