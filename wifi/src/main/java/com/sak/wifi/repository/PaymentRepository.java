package com.sak.wifi.repository;

import com.sak.wifi.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    List<Payment> findByCompanyId(Long companyId);

    Optional<Payment> findByIdAndCompanyId(
            Long id,
            Long companyId
    );

}
