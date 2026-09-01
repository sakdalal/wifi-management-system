package com.sak.wifi.service;

import com.sak.wifi.config.TenantContext;
import com.sak.wifi.dto.BillResponseDTO;
import com.sak.wifi.dto.PaymentRequestDTO;
import com.sak.wifi.dto.PaymentResponseDTO;
import com.sak.wifi.entity.*;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.BillRepository;
import com.sak.wifi.repository.CustomerRepository;

import com.sak.wifi.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {

    private final CustomerRepository customerRepository;
    private final BillRepository billRepository;
    private final ModelMapper modelMapper;
    private final PaymentRepository paymentRepository;


    @Transactional
    public BillResponseDTO generateBill(Long customerId){
        Long companyId= TenantContext.getCompanyId();
        Customer customer= customerRepository.findById(customerId)
                .orElseThrow(()->new ResourceNotFoundException("No Customer found"));

        if(customer.getPlan()==null){
            throw new IllegalArgumentException("The customer has no assigned plan");
        }
        if (!customer.getCompany().getId().equals(companyId)) {
            throw new IllegalArgumentException("Customer does not belong to your company");
        }

        Plan plan =customer.getPlan();
        Bill bill=new Bill();

        bill.setCustomer(customer);
        bill.setPlan(plan);
        bill.setAmount(plan.getPrice());
        bill.setBillingDate(LocalDate.now());
        bill.setBillingMonth(YearMonth.now().toString());
        bill.setDueDate(LocalDate.now().plusDays(15));
        bill.setPaymentStatus(PaymentStatus.PENDING);
        billRepository.save(bill);

        return modelMapper.map(bill, BillResponseDTO.class);

    }

    public List<BillResponseDTO> getAllBills(){
        Long companyId= TenantContext.getCompanyId();
        return billRepository.findByCustomerCompanyId(companyId)
                .stream()
                .map(bill -> modelMapper.map(bill, BillResponseDTO.class))
                .toList();
    }

    public List<BillResponseDTO> getBillsByCustomer(Long customerID){

        return billRepository.findByCustomerId(customerID)
                .stream()
                .map(bill -> modelMapper.map(bill,BillResponseDTO.class))
                .toList();
    }

    @Transactional
    public PaymentResponseDTO payBill(Long billId,
                                      PaymentRequestDTO request){
        Bill bill= billRepository.findById(billId)
                .orElseThrow(()->new ResourceNotFoundException("Bill not found"));

        if(bill.getPaymentStatus()==PaymentStatus.PAID){
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


    @Transactional
    public void generateMonthlyBills(){
        Long companyId=TenantContext.getCompanyId();
        List<Customer> customers = customerRepository.findByStatusAndCompanyId(CustomerStatus.ACTIVE,companyId);
        String billingMonth= YearMonth.now().toString();

        for(Customer customer:customers){
            boolean exists=billRepository.existsByCustomerIdAndBillingMonth(customer.getId(),billingMonth);

            if(exists)
                continue;

            Bill bill=Bill.builder()
                    .customer(customer)
                    .plan(customer.getPlan())
                    .amount(customer.getPlan().getPrice())
                    .billingDate(LocalDate.now())
                    .billingMonth(billingMonth)
                    .dueDate(LocalDate.now().plusDays(15))
                    .paymentStatus(PaymentStatus.PENDING)
                    .build();

            billRepository.save(bill);
        }

    }


}
