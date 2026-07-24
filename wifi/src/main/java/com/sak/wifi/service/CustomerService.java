package com.sak.wifi.service;

import com.sak.wifi.config.ModelMapperConfig;
import com.sak.wifi.dto.CustomerRequestDTO;
import com.sak.wifi.dto.CustomerResponseDTO;
import com.sak.wifi.dto.PageResponseDTO;
import com.sak.wifi.entity.Company;
import com.sak.wifi.entity.Customer;
import com.sak.wifi.entity.CustomerStatus;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.CompanyRepository;
import com.sak.wifi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final ModelMapper mapper;

    public CustomerResponseDTO createCustomer(CustomerRequestDTO request){

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));


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
        Customer customer=customerRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Customer Not Found with id: "+id));

        return mapper.map(customer,CustomerResponseDTO.class);
    }

    public PageResponseDTO<CustomerResponseDTO> getAllCustomer(int page,
                                                               int size,
                                                               String sortBy,
                                                               String direction){

        Sort sort=direction.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

        Pageable pageable= PageRequest.of(page,size,sort);

        Page<Customer> customerPage=customerRepository.findAll(pageable);

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

        return mapper.map(customer,CustomerResponseDTO.class);


    }

    public List<CustomerResponseDTO> searchCustomers(String keyword){

        return customerRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(customer -> mapper.map(customer,CustomerResponseDTO.class))
                .toList();
    }

    public List<CustomerResponseDTO> findCustomers(CustomerStatus status, Long companyId){

        List<Customer> customers;

        if(status!=null && companyId!=null){
            customers=customerRepository.findByStatusAndCompanyId(status,companyId);
        } else if (status!=null) {
            customers=customerRepository.findByStatus(status);
        } else if (companyId!=null) {
            customers=customerRepository.findByCompanyId(companyId);
        } else {
            customers=customerRepository.findAll();
        }

        return customers.stream()
                .map(customer -> mapper.map(customer,CustomerResponseDTO.class))
                .toList();

    }

}
