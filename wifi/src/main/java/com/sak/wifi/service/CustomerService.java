package com.sak.wifi.service;

import com.sak.wifi.config.TenantContext;
import com.sak.wifi.dto.CustomerRequestDTO;
import com.sak.wifi.dto.CustomerResponseDTO;
import com.sak.wifi.dto.PageResponseDTO;
import com.sak.wifi.entity.*;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.CompanyRepository;
import com.sak.wifi.repository.CustomerRepository;
import com.sak.wifi.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.nio.file.Files;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final PlanRepository planRepository;
    private final ModelMapper mapper;

    @CacheEvict(
            value = "dashboard",
            key="T(com.sak.wifi.config.TenantContext).getCompanyId()"
    )
    public CustomerResponseDTO createCustomer(CustomerRequestDTO request){

        Long companyId= TenantContext.getCompanyId();
        Company company= companyRepository.findById(companyId)
                .orElseThrow(()->new ResourceNotFoundException("Company not found"));

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setStatus(request.getStatus());
        customer.setInstallationDate(request.getInstallationDate());
        customer.setProfileImageUrl(request.getProfileImageUrl());
        customer.setCompany(company);

        Customer saved= customerRepository.save(customer);

        return mapper.map(saved,CustomerResponseDTO.class);
    }


    public CustomerResponseDTO getCustomer(Long id){
        Long companyId= TenantContext.getCompanyId();
        Company company= companyRepository.findById(companyId)
                .orElseThrow(()->new ResourceNotFoundException("Company not found"));

        Customer customer=customerRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(()->new ResourceNotFoundException("Customer Not Found with id: "+id));

        return mapper.map(customer,CustomerResponseDTO.class);
    }

    public PageResponseDTO<CustomerResponseDTO> getAllCustomer(int page,
                                                               int size,
                                                               String sortBy,
                                                               String direction){

        Long companyId= TenantContext.getCompanyId();
        Company company= companyRepository.findById(companyId)
                .orElseThrow(()->new ResourceNotFoundException("Company not found"));


        Sort sort=direction.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

        Pageable pageable= PageRequest.of(page,size,sort);

        Page<Customer> customerPage=customerRepository.findByCompanyId(companyId,pageable);

        List<CustomerResponseDTO> customers=customerPage
                .getContent()
                .stream()
                .map(customer -> mapper.map(customer,CustomerResponseDTO.class))
                .toList();

        return new PageResponseDTO<>(
                customers,
                customerPage.getNumber(),
                customerPage.getTotalPages(),
                customerPage.getTotalElements(),
                customerPage.getSize()
        );
    }

    @CacheEvict(
            value = "dashboard",
            key="T(com.sak.wifi.config.TenantContext).getCompanyId()"
    )
    public void deleteCustomer(Long id){
        Long companyId= TenantContext.getCompanyId();

        Customer customer=customerRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(()-> new ResourceNotFoundException("Customer Not Found"));

        customerRepository.delete(customer);
    }

    @CacheEvict(
            value = "dashboard",
            key="T(com.sak.wifi.config.TenantContext).getCompanyId()"
    )
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO request){

        Long companyId= TenantContext.getCompanyId();
        Company company= companyRepository.findById(companyId)
                .orElseThrow(()->new ResourceNotFoundException("Company not found"));


        Customer customer=customerRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(()-> new ResourceNotFoundException("Customer not Found"));

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        customer.setStatus(request.getStatus());
        customer.setInstallationDate(request.getInstallationDate());
        customer.setProfileImageUrl(request.getProfileImageUrl());

        customer=customerRepository.save(customer);

        return mapper.map(customer,CustomerResponseDTO.class);


    }

    public List<CustomerResponseDTO> searchCustomers(String keyword){

        Long companyId= TenantContext.getCompanyId();
        Company company= companyRepository.findById(companyId)
                .orElseThrow(()->new ResourceNotFoundException("Company not found"));


        return customerRepository.findByNameContainingIgnoreCase(companyId,keyword)
                .stream()
                .map(customer -> mapper.map(customer,CustomerResponseDTO.class))
                .toList();
    }

    public List<CustomerResponseDTO> findCustomers(CustomerStatus status){
        Long companyId = TenantContext.getCompanyId();

        List<Customer> customers;

        if(status!=null){
            customers=customerRepository.findByStatusAndCompanyId(status,companyId);
        }else {
            customers=customerRepository.findByCompanyId(companyId);
        }

        return customers.stream()
                .map(customer -> mapper.map(customer,CustomerResponseDTO.class))
                .toList();

    }

    @CacheEvict(
            value = "dashboard",
            key="T(com.sak.wifi.config.TenantContext).getCompanyId()"
    )
    public CustomerResponseDTO uploadImage(Long id, MultipartFile file){

        Long companyId= TenantContext.getCompanyId();

        Customer customer= customerRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(()->new RuntimeException("Customer not found"));

        if(file.isEmpty()){
            throw new RuntimeException("File is empty");
        }

        if(!file.getContentType().equals("image/png") &&
                !file.getContentType().equals("image/jpeg")){
            throw new RuntimeException("Only PNG and JPEG files allowed");
        }

        if(file.getSize()> 2*1024*1024){
            throw new RuntimeException("Max size 2MB");
        }

        try{

            String fileName= System.currentTimeMillis()+"_"+file.getOriginalFilename();
            Path uploadPath= Paths.get("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath=uploadPath.resolve(fileName);
            file.transferTo(filePath);
            customer.setProfileImageUrl("/uploads/"+fileName);
            customerRepository.save(customer);
            return mapper.map(customer,CustomerResponseDTO.class);


        } catch (Exception e) {
            throw new RuntimeException("Image upload failed");
        }

    }


    public CustomerResponseDTO assignPlan(Long customerId, Long planId){

        Long companyId= TenantContext.getCompanyId();

        Customer customer= customerRepository.findByIdAndCompanyId(customerId,companyId)
                .orElseThrow(()->new RuntimeException("Customer Not Found"));

        Plan plan = planRepository.findByIdAndCompanyId(planId,companyId)
                .orElseThrow(()->new RuntimeException("No such plan exists"));

        if(plan.getPlanStatus()!= PlanStatus.ACTIVE){
            throw new IllegalArgumentException("Only active plans can be assigned");
        }

        if(customer.getPlan()!=null &&
                customer.getPlan().getId().equals(planId)){
            throw new IllegalArgumentException("Customer already has this plan");
        }

        customer.setPlan(plan);
        Customer saved=customerRepository.save(customer);

        CustomerResponseDTO dto= mapper.map(saved, CustomerResponseDTO.class);
        if(customer.getPlan()!=null){
            dto.setCurrentPlan(customer.getPlan().getPlanName());
            dto.setSpeed(customer.getPlan().getSpeedMbps());
            dto.setPrice(customer.getPlan().getPrice());
        }
        return dto;

    }


    @CacheEvict(
            value = "dashboard",
            key="T(com.sak.wifi.config.TenantContext).getCompanyId()"
    )
    public CustomerResponseDTO upgradePlan(Long customerId, Long planId){

        Long companyId= TenantContext.getCompanyId();

        Customer customer= customerRepository.findByIdAndCompanyId(customerId,companyId)
                .orElseThrow(()->new RuntimeException("Customer Not Found"));

        Plan plan = planRepository.findByIdAndCompanyId(planId,companyId)
                .orElseThrow(()->new RuntimeException("No such plan exists"));

        if(plan.getPlanStatus()!= PlanStatus.ACTIVE){
            throw new IllegalArgumentException("Only active plans can be assigned");
        }

        if(customer.getPlan()!=null &&
                customer.getPlan().getId().equals(planId)){
            throw new IllegalArgumentException("Customer already has this plan");
        }

        BigDecimal currentPrice= customer.getPlan().getPrice();
        BigDecimal newPrice=plan.getPrice();

        if(newPrice.compareTo(currentPrice)<=0){
            throw new IllegalArgumentException("Upgrade can only happen to a higher priced plan");
        }

        customer.setPlan(plan);
        Customer saved=customerRepository.save(customer);

        CustomerResponseDTO dto= mapper.map(saved, CustomerResponseDTO.class);
        if(customer.getPlan()!=null){
            dto.setCurrentPlan(customer.getPlan().getPlanName());
            dto.setSpeed(customer.getPlan().getSpeedMbps());
            dto.setPrice(customer.getPlan().getPrice());
        }
        return dto;

    }

    @CacheEvict(
            value = "dashboard",
            key="T(com.sak.wifi.config.TenantContext).getCompanyId()"
    )
    public CustomerResponseDTO downgradePlan(Long customerId, Long planId){

        Long companyId= TenantContext.getCompanyId();

        Customer customer= customerRepository.findByIdAndCompanyId(customerId,companyId)
                .orElseThrow(()->new RuntimeException("Customer Not Found"));

        Plan plan = planRepository.findByIdAndCompanyId(planId,companyId)
                .orElseThrow(()->new RuntimeException("No such plan exists"));

        if(plan.getPlanStatus()!= PlanStatus.ACTIVE){
            throw new IllegalArgumentException("Only active plans can be assigned");
        }

        if(customer.getPlan()!=null &&
                customer.getPlan().getId().equals(planId)){
            throw new IllegalArgumentException("Customer already has this plan");
        }

        BigDecimal currentPrice= customer.getPlan().getPrice();
        BigDecimal newPrice=plan.getPrice();

        if(newPrice.compareTo(currentPrice)>=0){
            throw new IllegalArgumentException("Upgrade can only happen to a higher priced plan");
        }

        customer.setPlan(plan);
        Customer saved=customerRepository.save(customer);

        CustomerResponseDTO dto= mapper.map(saved, CustomerResponseDTO.class);
        if(customer.getPlan()!=null){
            dto.setCurrentPlan(customer.getPlan().getPlanName());
            dto.setSpeed(customer.getPlan().getSpeedMbps());
            dto.setPrice(customer.getPlan().getPrice());
        }
        return dto;

    }

}
