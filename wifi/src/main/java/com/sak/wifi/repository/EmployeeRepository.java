package com.sak.wifi.repository;

import com.sak.wifi.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {


    List<Employee> findByCompanyId(Long companyId);

    Optional<Employee> findByIdAndCompanyId(Long id, Long companyId);

    boolean existsByEmailAndCompanyId(String email, Long companyId);

}
