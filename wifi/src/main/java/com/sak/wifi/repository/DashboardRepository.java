package com.sak.wifi.repository;

import com.sak.wifi.entity.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.Repository;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public interface DashboardRepository extends Repository<Customer,Long> {

    @Query(value = """
            SELECT COUNT(*)
            FROM customers
            WHERE company_id = :companyId
            """, nativeQuery = true)
    Long getTotalCustomers(@Param("companyId") Long companyId);

    @Query(value = """
        SELECT COUNT(*)
        FROM customers
        WHERE company_id = :companyId
        AND installation_date >= :startDate
        """, nativeQuery = true)
    Long getNewCustomers(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDate startDate
    );

    @Query(value = """
        SELECT COALESCE(SUM(amount), 0)
        FROM payments
        WHERE company_id = :companyId
        """, nativeQuery = true)
    BigDecimal getTotalRevenue(
            @Param("companyId") Long companyId
    );

    @Query(value = """
        SELECT
            DATE_TRUNC('month', payment_date) AS month,
            COALESCE(SUM(amount), 0) AS revenue
        FROM payments
        WHERE company_id = :companyId
        GROUP BY DATE_TRUNC('month', payment_date)
        ORDER BY DATE_TRUNC('month', payment_date)
        """, nativeQuery = true)
    List<Object[]> getMonthlyRevenue(
            @Param("companyId") Long companyId
    );

    @Query(value = """
        SELECT
            DATE_TRUNC('month', payment_date) AS month,
            COALESCE(SUM(amount), 0) AS revenue
        FROM payments
        WHERE company_id = :companyId
        AND payment_date >= :startDate
        GROUP BY DATE_TRUNC('month', payment_date)
        ORDER BY DATE_TRUNC('month', payment_date)
        """, nativeQuery = true)
    List<Object[]> getMonthlyRevenue(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDate startDate
    );

    @Query(value = """
        SELECT
            p.id,
            p.plan_name,
            COUNT(c.id)
        FROM customers c
        JOIN plans p ON c.plan_id = p.id
        WHERE c.company_id = :companyId
        GROUP BY p.id, p.plan_name
        ORDER BY COUNT(c.id) DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> getTopPlans(
            @Param("companyId") Long companyId
    );

    @Query(value = """
        SELECT status, COUNT(*)
        FROM complaints
        WHERE company_id = :companyId
        GROUP BY status
        """, nativeQuery = true)
    List<Object[]> getComplaintStats(
            @Param("companyId") Long companyId
    );

    @Query(value = """
        SELECT
            DATE_TRUNC('month', installation_date) AS month,
            COUNT(*) AS customer_count
        FROM customers
        WHERE company_id = :companyId
        AND installation_date >= :startDate
        GROUP BY DATE_TRUNC('month', installation_date)
        ORDER BY DATE_TRUNC('month', installation_date)
        """, nativeQuery = true)
    List<Object[]> getCustomerGrowth(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDate startDate
    );
}
