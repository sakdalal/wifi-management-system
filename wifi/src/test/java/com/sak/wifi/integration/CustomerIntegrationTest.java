package com.sak.wifi.integration;

import com.sak.wifi.dto.CustomerRequestDTO;
import com.sak.wifi.entity.Company;
import com.sak.wifi.entity.Customer;
import com.sak.wifi.entity.CustomerStatus;
import com.sak.wifi.repository.CompanyRepository;
import com.sak.wifi.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ObjectMapper objectMapper;


    private Company createTestCompany(){
        Company company=new Company();
        company.setCompanyName("Test ISP");
        company.setEmail("test@isp.com");

        return companyRepository.save(company);
    }

    private Customer createTestCustomer(Company company){
        Customer customer=new Customer();
        customer.setName("Dean");
        customer.setEmail("dean@test.com");
        customer.setPhone("1234567890");
        customer.setAddress("Street");
        customer.setCompany(company);
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setEnabled(true);

        return customerRepository.save(customer);
    }

    private CustomerRequestDTO createCustomerRequest() {

        CustomerRequestDTO request = new CustomerRequestDTO();

        request.setName("John");
        request.setEmail("john@test.com");
        request.setPhone("9876543210");
        request.setAddress("New Street");
        request.setStatus(CustomerStatus.ACTIVE);

        return request;
    }

    @Test
    void shouldGetCustomerById() throws Exception{

        Company company=createTestCompany();
        Customer customer=createTestCustomer(company);

        mockMvc.perform(get("/customers/"+customer.getId())
                        .header("X-Company-Id", company.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dean"))
                .andExpect(jsonPath("$.email").value("dean@test.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }


    @Test
    void shouldCreateCustomer() throws Exception{

        Company company=createTestCompany();
        CustomerRequestDTO request=createCustomerRequest();
        mockMvc.perform(post("/customers")
                        .header("X-Company-Id", company.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

    }


    @Test
    @WithMockUser
    void shouldGetAllCustomers() throws Exception{
        Company company=createTestCompany();
        createTestCustomer(company);

        Customer customer2 = new Customer();
        customer2.setName("John");
        customer2.setEmail("john@test.com");
        customer2.setPhone("9876543210");
        customer2.setAddress("Another Street");
        customer2.setCompany(company);
        customer2.setStatus(CustomerStatus.ACTIVE);
        customer2.setEnabled(true);

        customerRepository.save(customer2);

        mockMvc.perform(get("/customers")
                .header("X-Company-Id",company.getId())
                .param("page","0")
                .param("size","10")
                .param("sortBy","name")
                .param("direction","asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

    }


    @Test
    void shouldUpdateCustomerById() throws Exception{

        Company company = createTestCompany();
        Customer customer = createTestCustomer(company);
        CustomerRequestDTO request = createCustomerRequest();

        request.setName("Updated Dean");
        request.setEmail("updated@test.com");

        mockMvc.perform(put("/customers/"+customer.getId())
                .header("X-Company-Id",company.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Dean"))
                .andExpect(jsonPath("$.email").value("updated@test.com"));

    }


    @Test
    void shouldDeleteCustomerWithId() throws Exception{
        Company company=createTestCompany();
        Customer customer=createTestCustomer(company);

        mockMvc.perform(delete("/customers/"+customer.getId())
                .header("X-Company-Id",company.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer deleted"));

        //making sure customer doesn't exist anymore
        assertFalse(
                customerRepository.existsById(customer.getId())
        );

    }


    @Test
    void shouldSearchCustomer()throws Exception{
        Company company=createTestCompany();
        Customer customer=createTestCustomer(company);

        mockMvc.perform(get("/customers/search")
                .header("X-Company-Id",company.getId())
                .param("keyword","Dean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Dean"));

    }

    @Test
    void shouldFindCustomerByStatus()throws Exception{
        Company company = createTestCompany();

        createTestCustomer(company);

        Customer inactiveCustomer = new Customer();

        inactiveCustomer.setName("John");
        inactiveCustomer.setEmail("john@test.com");
        inactiveCustomer.setPhone("9876543210");
        inactiveCustomer.setAddress("Another Street");
        inactiveCustomer.setCompany(company);
        inactiveCustomer.setStatus(CustomerStatus.INACTIVE);
        inactiveCustomer.setEnabled(false);

        customerRepository.save(inactiveCustomer);

        mockMvc.perform(get("/customers/find")
                .header("X-Company-Id",company.getId())
                .param("status","ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

    }

    @Test
    void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {

        Company company = createTestCompany();

        mockMvc.perform(get("/customers/99999")
                        .header("X-Company-Id", company.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }


    @Test
    void shouldReturnBadRequestForInvalidCustomer() throws Exception {

        Company company = createTestCompany();

        CustomerRequestDTO request = new CustomerRequestDTO();

        request.setName("");
        request.setEmail("invalid-email");
        request.setPhone("");

        mockMvc.perform(post("/customers")
                        .header("X-Company-Id", company.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
