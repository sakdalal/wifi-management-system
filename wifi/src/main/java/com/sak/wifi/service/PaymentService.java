package com.sak.wifi.service;

import com.sak.wifi.config.TenantContext;
import com.sak.wifi.entity.Payment;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void createPayments(){

    }

    public List<Payment> getAllPayments() {

        Long companyId = TenantContext.getCompanyId();
        return paymentRepository.findByCompanyId(companyId);
    }

    public Payment getPayment(Long id) {

        Long companyId = TenantContext.getCompanyId();

        return paymentRepository
                .findByIdAndCompanyId(id, companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"
                        )
                );
    }
}
