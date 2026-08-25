package com.sak.wifi.service;

import com.sak.wifi.config.TenantContext;
import com.sak.wifi.dto.EmployeeRequestDTO;
import com.sak.wifi.entity.Company;
import com.sak.wifi.entity.Employee;
import com.sak.wifi.repository.CompanyRepository;
import com.sak.wifi.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;

    public Employee createEmployee(EmployeeRequestDTO request) {

        Long companyId = TenantContext.getCompanyId();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (employeeRepository.existsByEmailAndCompanyId(
                request.getEmail(), companyId)) {

            throw new RuntimeException("Employee with this email already exists");
        }

        Employee employee = Employee.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .company(company)
                .build();

        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {

        Long companyId = TenantContext.getCompanyId();

        return employeeRepository.findByCompanyId(companyId);
    }

    public Employee getEmployeeById(Long id) {

        Long companyId = TenantContext.getCompanyId();

        return employeeRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));
    }

    public Employee updateEmployee(
            Long id,
            EmployeeRequestDTO request) {

        Long companyId = TenantContext.getCompanyId();

        Employee employee = employeeRepository
                .findByIdAndCompanyId(id, companyId)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());

        return employeeRepository.save(employee);
    }

    public String deleteEmployee(Long id) {

        Long companyId = TenantContext.getCompanyId();

        Employee employee = employeeRepository
                .findByIdAndCompanyId(id, companyId)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        employeeRepository.delete(employee);

        return "Employee deleted successfully";
    }
}
