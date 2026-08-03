package com.sak.wifi.dto;

import com.sak.wifi.entity.ComplaintPriority;
import com.sak.wifi.entity.ComplaintStatus;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintResponseDTO {

    private Long id;

    private String title;
    private String description;
    private String assignedEmployee;
    private Long customerId;
    private String customerName;
    private ComplaintPriority priority;
    private ComplaintStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
