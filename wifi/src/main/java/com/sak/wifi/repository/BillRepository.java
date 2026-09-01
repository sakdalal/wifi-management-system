package com.sak.wifi.repository;

import com.sak.wifi.entity.Bill;
import com.sak.wifi.entity.Customer;
import com.sak.wifi.entity.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill,Long> {

    List<Bill> findByCustomerId(Long customerId);

    List<Bill> findByCustomerCompanyId(Long companyId);

    boolean existsByCustomerIdAndBillingMonth(Long customerId, String billingMonth);

}
