package com.sak.wifi.service;

import com.sak.wifi.dto.BillResponseDTO;
import com.sak.wifi.entity.*;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.BillRepository;
import com.sak.wifi.repository.CustomerRepository;
import com.sak.wifi.repository.PlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {

    private final CustomerRepository customerRepository;
    private final PlanRepository planRepository;
    private final BillRepository billRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public BillResponseDTO generateBill(Long customerId){
        Customer customer= customerRepository.findById(customerId)
                .orElseThrow(()->new ResourceNotFoundException("No Customer found"));

        if(customer.getPlan()==null){
            throw new IllegalArgumentException("The customer has no assigned plan");
        }

        Plan plan =customer.getPlan();
        Bill bill=new Bill();

        bill.setCustomer(customer);
        bill.setPlan(plan);
        bill.setAmount(plan.getPrice());
        bill.setBillingDate(LocalDate.now());
        bill.setBillingMonth(LocalDate.now().getMonth().name());
        bill.setDueDate(LocalDate.now().plusDays(15));
        bill.setPaymentStatus(PaymentStatus.PENDING);
        billRepository.save(bill);

        return modelMapper.map(bill, BillResponseDTO.class);

    }

    public List<BillResponseDTO> getAllBills(){
        return billRepository.findAll()
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
    public BillResponseDTO payBill(Long billId){
        Bill bill= billRepository.findById(billId)
                .orElseThrow(()->new ResourceNotFoundException("Bill not found"));

        if(bill.getPaymentStatus()==PaymentStatus.PAID){
            throw new IllegalArgumentException("Bill Already Exists");
        }

        bill.setPaymentStatus(PaymentStatus.PAID);
        billRepository.save(bill);
        return modelMapper.map(bill, BillResponseDTO.class);

    }


    @Transactional
    public void generateMonthlyBills(){

        List<Customer> customers = customerRepository.findByStatus(CustomerStatus.ACTIVE);
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
