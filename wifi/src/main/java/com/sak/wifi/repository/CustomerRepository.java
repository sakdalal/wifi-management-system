package com.sak.wifi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sak.wifi.entity.Customer;
import com.sak.wifi.entity.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

    List<Customer> findByStatus(CustomerStatus status);

    List<Customer> findByCompanyIdAndNameContainingIgnoreCase(Long companyId,String keyword);

    List<Customer> findByCompanyId(Long companyId);

    Page<Customer> findByCompanyId(Long companyId, Pageable pageable);

    Optional<Customer> findByIdAndCompanyId(
            Long id,
            Long companyId
    );

    List<Customer> findByStatusAndCompanyId(CustomerStatus status,
                                            Long companyId);

}
