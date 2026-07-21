package com.sak.wifi.service;

import com.sak.wifi.dto.CustomerRequestDTO;
import com.sak.wifi.dto.CustomerResponseDTO;
import com.sak.wifi.entity.Company;
import com.sak.wifi.entity.Customer;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.CompanyRepository;
import com.sak.wifi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;

    public CustomerResponseDTO createCustomer(CustomerRequestDTO request){

        Company company=companyRepository.findById(request.getCompanyId())
                .orElseThrow(()->new ResourceNotFoundException("Company Not Found"));

        Customer customer= Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .installationDate(request.getInstallationDate())
                .profileImageUrl(request.getProfileImageUrl())
                .status(request.getStatus())
                .company(company)
                .build();

        customer= customerRepository.save(customer);

        return mapToResponse(customer);
    }

    private CustomerResponseDTO mapToResponse(Customer customer){

        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .phone(customer.getPhone())
                .status(customer.getStatus())
                .installationDate(customer.getInstallationDate())
                .profileImageUrl(customer.getProfileImageUrl())
                .companyId(customer.getCompany().getId())
                .build();
    }

    public CustomerResponseDTO getCustomer(Long id){
        Customer customer=customerRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Customer Not Found"));

        return mapToResponse(customer);
    }

    public List<CustomerResponseDTO> getAllCustomer(){
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteCustomer(Long id){
        Customer customer=customerRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Customer Not Found"));

        customerRepository.delete(customer);
    }

    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO request){

        Customer customer=customerRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Customer not Found"));

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        customer.setStatus(request.getStatus());
        customer.setInstallationDate(request.getInstallationDate());
        customer.setProfileImageUrl(request.getProfileImageUrl());

        customer=customerRepository.save(customer);

        return mapToResponse(customer);


    }

}
