package com.sak.wifi.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponseDTO implements Serializable {

    private Long openComplaints;
    private Long highPriorityComplaints;
    private Long pendingMoreThanThreeDays;

    private Long totalCustomers;
    private Long newCustomers;
    private BigDecimal totalRevenue;
    private List<MonthlyRevenueDTO> monthlyRevenue;
    private List<TopPlanDTO> topPlans;
    private ComplaintStatsDTO complaintStats;
    private List<CustomerGrowthDTO> customerGrowth;
}
