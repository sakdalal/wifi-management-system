package com.sak.wifi.repository;

import com.sak.wifi.entity.Customer;
import com.sak.wifi.entity.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

    List<Customer> findByStatus(CustomerStatus status);

    List<Customer> findByNameContainingIgnoreCase(String keyword);

    Optional<Customer> findByEmail(String email);

    List<Customer> findByCompanyId(Long companyId);

    List<Customer> findByStatusAndCompanyId(CustomerStatus status,
                                            Long companyId);

}
