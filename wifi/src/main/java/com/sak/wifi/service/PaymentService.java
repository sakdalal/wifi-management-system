package com.sak.wifi.service;

import com.sak.wifi.config.TenantContext;
import com.sak.wifi.dto.PaymentRequestDTO;
import com.sak.wifi.dto.PaymentResponseDTO;
import com.sak.wifi.entity.Bill;
import com.sak.wifi.entity.Payment;
import com.sak.wifi.entity.PaymentStatus;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.BillRepository;
import com.sak.wifi.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private  final BillRepository billRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public PaymentResponseDTO payBill(Long billId,
                                      PaymentRequestDTO request){

        Long companyId= TenantContext.getCompanyId();

        Bill bill= billRepository.findById(billId)
                .orElseThrow(()->new ResourceNotFoundException("Bill not found"));

        if(!bill.getCustomer().getCompany().getId().equals(companyId)){
            throw new IllegalArgumentException("Bill does not belong to your company");
        }

        if(bill.getPaymentStatus()== PaymentStatus.PAID){
            throw new IllegalArgumentException("This Bill has Already been paid");
        }

        Payment payment=Payment.builder()
                .amount(bill.getAmount())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PAID)
                .transactionId(request.getTransactionId())
                .customer(bill.getCustomer())
                .company(bill.getCustomer().getCompany())
                .bill(bill)
                .build();

        paymentRepository.save(payment);

        bill.setPaymentStatus(PaymentStatus.PAID);
        billRepository.save(bill);
        return modelMapper.map(payment, PaymentResponseDTO.class);

    }

    public List<PaymentResponseDTO> getAllPayments() {

        Long companyId = TenantContext.getCompanyId();
        return paymentRepository.findByCompanyId(companyId)
                .stream()
                .map(payment -> modelMapper.map(payment,PaymentResponseDTO.class))
                .toList();
    }

    public PaymentResponseDTO getPayment(Long id) {

        Long companyId = TenantContext.getCompanyId();

        Payment payment= paymentRepository
                .findByIdAndCompanyId(id, companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"
                        )
                );

        return modelMapper.map(payment, PaymentResponseDTO.class);
    }
}
