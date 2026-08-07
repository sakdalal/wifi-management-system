package com.sak.wifi.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponseDTO {

    private Long openComplaints;
    private Long highPriorityComplaints;
    private Long pendingMoreThanThreeDays;
}
