package com.sak.wifi.service;

import com.sak.wifi.config.TenantContext;
import com.sak.wifi.dto.CustomerRequestDTO;
import com.sak.wifi.dto.CustomerResponseDTO;
import com.sak.wifi.entity.Company;
import com.sak.wifi.entity.Customer;
import com.sak.wifi.entity.CustomerStatus;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.CompanyRepository;
import com.sak.wifi.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private CustomerService customerService;

    private Long companyId;
    private Company company;
    private Customer customer;
    private CustomerRequestDTO request;
    private CustomerResponseDTO response;

    @BeforeEach
    void setUp(){

        companyId=1L;
        TenantContext.setCompanyId(companyId);

        company =new Company();
        company.setId(companyId);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Dean");
        customer.setEmail("dean@example.com");
        customer.setPhone("9876543210");
        customer.setAddress("street");
        customer.setStatus(CustomerStatus.ACTIVE);

        request = new CustomerRequestDTO();
        request.setName("Dean");
        request.setEmail("dean@example.com");
        request.setPhone("9876543210");
        request.setAddress("street");
        request.setStatus(CustomerStatus.ACTIVE);

        response = new CustomerResponseDTO();
        response.setId(1L);
        response.setName("Dean");
        response.setEmail("dean@example.com");
        response.setStatus(CustomerStatus.ACTIVE);

    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldCreateCustomerSuccessfully(){


        //Arrange
        when(companyRepository.findById(companyId))
                    .thenReturn(Optional.of(company));

        when(customerRepository.save(any(Customer.class)))
                    .thenReturn(customer);
        when(mapper.map(customer, CustomerResponseDTO.class))
                    .thenReturn(response);

        //Act
        CustomerResponseDTO result=
                    customerService.createCustomer(request);

        //Assert
        assertNotNull(result);
        assertEquals("Dean",result.getName());
        assertEquals("dean@example.com",result.getEmail());
        assertEquals(CustomerStatus.ACTIVE, result.getStatus());

        //Verify
        verify(companyRepository)
                .findById(companyId);
        verify(customerRepository)
                .save(any(Customer.class));
        verify(mapper)
                .map(customer,CustomerResponseDTO.class);
    }


    @Test
    void shouldThrowExceptionWhenCompanyNotFoundWhileCreatingCustomer() {

        // Arrange
        when(companyRepository.findById(companyId))
                .thenReturn(Optional.empty());


        // Act + Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.createCustomer(request)
        );


        // Verify
        verify(companyRepository)
                .findById(companyId);

        verify(customerRepository, never())
                .save(any(Customer.class));

        verifyNoInteractions(mapper);
    }


    @Test
    void shouldGetCustomerSuccessfully() {

        // Arrange
        Long customerId = 1L;
        when(companyRepository.findById(companyId))
                .thenReturn(Optional.of(company));
        when(customerRepository.findByIdAndCompanyId(
                customerId,
                companyId
        )).thenReturn(Optional.of(customer));
        when(mapper.map(customer, CustomerResponseDTO.class))
                .thenReturn(response);


        // Act
        CustomerResponseDTO result =
                customerService.getCustomer(customerId);


        // Assert
        assertNotNull(result);
        assertEquals(
                customerId,
                result.getId()
        );
        assertEquals(
                "Dean",
                result.getName()
        );


        // Verify
        verify(companyRepository)
                .findById(companyId);
        verify(customerRepository)
                .findByIdAndCompanyId(
                        customerId,
                        companyId
                );
        verify(mapper)
                .map(customer, CustomerResponseDTO.class);
    }


    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {

        // Arrange
        Long customerId = 99L;

        when(companyRepository.findById(companyId))
                .thenReturn(Optional.of(company));

        when(customerRepository.findByIdAndCompanyId(
                customerId,
                companyId
        )).thenReturn(Optional.empty());


        // Act + Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomer(customerId)
        );


        // Verify
        verify(customerRepository)
                .findByIdAndCompanyId(
                        customerId,
                        companyId
                );

        verifyNoInteractions(mapper);
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {

        // Arrange
        Long customerId = 1L;
        CustomerRequestDTO updateRequest =
                new CustomerRequestDTO();
        updateRequest.setName("Updated Dean");
        updateRequest.setEmail("updated@dean.com");
        updateRequest.setPhone("9999999999");
        updateRequest.setAddress("New Enclave");
        updateRequest.setStatus(CustomerStatus.INACTIVE);


        CustomerResponseDTO updatedResponse =
                new CustomerResponseDTO();

        updatedResponse.setId(customerId);
        updatedResponse.setName("Updated Dean");
        updatedResponse.setEmail("updated@dean.com");
        updatedResponse.setStatus(CustomerStatus.INACTIVE);


        when(companyRepository.findById(companyId))
                .thenReturn(Optional.of(company));
        when(customerRepository.findByIdAndCompanyId(
                customerId,
                companyId
        )).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer))
                .thenReturn(customer);
        when(mapper.map(customer, CustomerResponseDTO.class))
                .thenReturn(updatedResponse);


        // Act
        CustomerResponseDTO result =
                customerService.updateCustomer(
                        customerId,
                        updateRequest
                );


        // Assert
        assertEquals(
                "Updated Dean",
                customer.getName()
        );
        assertEquals(
                "updated@dean.com",
                customer.getEmail()
        );

        assertEquals(
                "New Enclave",
                customer.getAddress()
        );

        assertEquals(
                "9999999999",
                customer.getPhone()
        );

        assertEquals(
                CustomerStatus.INACTIVE,
                customer.getStatus()
        );
        assertNotNull(result);
        assertEquals(
                "Updated Dean",
                result.getName()
        );
        assertEquals(
                "updated@dean.com",
                result.getEmail()
        );
        assertEquals(
                CustomerStatus.INACTIVE,
                result.getStatus()
        );


        // Verify
        verify(customerRepository)
                .save(customer);
    }


    @Test
    void shouldDeleteCustomerSuccessfully() {

        // Arrange
        Long customerId = 1L;
        when(customerRepository.findByIdAndCompanyId(
                customerId,
                companyId
        )).thenReturn(Optional.of(customer));


        // Act
        customerService.deleteCustomer(customerId);


        // Verify
        verify(customerRepository)
                .findByIdAndCompanyId(
                        customerId,
                        companyId
                );

        verify(customerRepository)
                .delete(customer);
    }


    @Test
    void shouldThrowExceptionWhenDeletingNonExistingCustomer() {

        // Arrange

        Long customerId = 99L;
        when(customerRepository.findByIdAndCompanyId(
                customerId,
                companyId
        )).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.deleteCustomer(customerId)
        );


        // Verify
        verify(customerRepository, never())
                .delete(any(Customer.class));
    }


}
