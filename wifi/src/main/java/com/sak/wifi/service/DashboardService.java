package com.sak.wifi.service;

import com.sak.wifi.config.TenantContext;
import com.sak.wifi.dto.*;
import com.sak.wifi.entity.ComplaintPriority;
import com.sak.wifi.entity.ComplaintStatus;
import com.sak.wifi.repository.ComplaintRepository;
import com.sak.wifi.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ComplaintRepository complaintRepository;
    private final DashboardRepository dashboardRepository;

    public DashboardResponseDTO getDashboardAll(){

        Long companyId = TenantContext.getCompanyId();

        long open= complaintRepository.countByStatusAndCompanyId(ComplaintStatus.OPEN,companyId);
        long priority=complaintRepository.countByPriorityAndCompanyId(ComplaintPriority.HIGH,companyId);
        long pending=complaintRepository.countByStatusAndCreatedAtBeforeAndCompanyId(ComplaintStatus.OPEN,
                LocalDateTime.now().minusDays(3),companyId);

        LocalDate now = LocalDate.now();

        LocalDate startOfCurrentMonth =
                now.withDayOfMonth(1);

        LocalDate startOfGrowthPeriod =
                now.minusMonths(11)
                        .withDayOfMonth(1);


        Long totalCustomers =
                dashboardRepository.getTotalCustomers(companyId);


        Long newCustomers =
                dashboardRepository.getNewCustomers(
                        companyId,
                        startOfCurrentMonth
                );


        BigDecimal totalRevenue =
                dashboardRepository.getTotalRevenue(companyId);


        List<MonthlyRevenueDTO> monthlyRevenue =
                getMonthlyRevenue(
                        companyId,
                        startOfGrowthPeriod
                );


        List<TopPlanDTO> topPlans =
                getTopPlans(companyId);


        ComplaintStatsDTO complaintStats =
                getComplaintStats(companyId);


        List<CustomerGrowthDTO> customerGrowth =
                getCustomerGrowth(
                        companyId,
                        startOfGrowthPeriod
                );

        return new DashboardResponseDTO(
                open,
                priority,
                pending,
                totalCustomers,
                newCustomers,
                totalRevenue,
                monthlyRevenue,
                topPlans,
                complaintStats,
                customerGrowth
        );
    }

    private List<MonthlyRevenueDTO> getMonthlyRevenue(
            Long companyId,
            LocalDate startDate
    ) {

        List<Object[]> results =
                dashboardRepository.getMonthlyRevenue(
                        companyId,
                        startDate
                );

        List<MonthlyRevenueDTO> response =
                new ArrayList<>();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM yyyy");

        for (Object[] row : results) {

            String month =
                    ((java.sql.Timestamp) row[0])
                            .toLocalDateTime()
                            .toLocalDate()
                            .format(formatter);

            BigDecimal revenue =
                    (BigDecimal) row[1];

            response.add(
                    new MonthlyRevenueDTO(
                            month,
                            revenue
                    )
            );
        }

        return response;
    }

    private List<TopPlanDTO> getTopPlans(Long companyId) {

        List<Object[]> results =
                dashboardRepository.getTopPlans(companyId);

        List<TopPlanDTO> response =
                new ArrayList<>();

        for (Object[] row : results) {

            Long planId =
                    ((Number) row[0]).longValue();

            String planName =
                    (String) row[1];

            Long customerCount =
                    ((Number) row[2]).longValue();

            response.add(
                    new TopPlanDTO(
                            planId,
                            planName,
                            customerCount
                    )
            );
        }

        return response;
    }

    private ComplaintStatsDTO getComplaintStats(Long companyId) {

        List<Object[]> results =
                dashboardRepository.getComplaintStats(companyId);

        long open = 0;
        long assigned = 0;
        long resolved = 0;
        long closed = 0;

        for (Object[] row : results) {

            String status = row[0].toString();

            long count =
                    ((Number) row[1]).longValue();

            switch (status) {

                case "OPEN" -> open = count;

                case "ASSIGNED" -> assigned = count;

                case "RESOLVED" -> resolved = count;

                case "CLOSED" -> closed = count;
            }
        }

        return new ComplaintStatsDTO(
                open,
                assigned,
                resolved,
                closed
        );
    }


    private List<CustomerGrowthDTO> getCustomerGrowth(
            Long companyId,
            LocalDate startDate
    ) {

        List<Object[]> results =
                dashboardRepository.getCustomerGrowth(
                        companyId,
                        startDate
                );

        List<CustomerGrowthDTO> response =
                new ArrayList<>();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM yyyy");

        for (Object[] row : results) {

            String month =
                    ((java.time.Instant) row[0])
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                            .format(formatter);
            Long customerCount =
                    ((Number) row[1]).longValue();

            response.add(
                    new CustomerGrowthDTO(
                            month,
                            customerCount
                    )
            );
        }

        return response;
    }
}
