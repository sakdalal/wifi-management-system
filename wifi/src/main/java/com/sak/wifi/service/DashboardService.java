package com.sak.wifi.service;

import com.sak.wifi.dto.DashboardResponseDTO;
import com.sak.wifi.entity.ComplaintPriority;
import com.sak.wifi.entity.ComplaintStatus;
import com.sak.wifi.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ComplaintRepository complaintRepository;

    public DashboardResponseDTO getDashboard(){
        long open= complaintRepository.countByStatus(ComplaintStatus.OPEN);
        long priority=complaintRepository.countByPriority(ComplaintPriority.HIGH);
        long pending=complaintRepository.countByStatusAndCreatedAtBefore(ComplaintStatus.OPEN,
                LocalDateTime.now().minusDays(3));

        return DashboardResponseDTO.builder()
                .openComplaints(open)
                .highPriorityComplaints(priority)
                .pendingMoreThanThreeDays(pending)
                .build();
    }
}
