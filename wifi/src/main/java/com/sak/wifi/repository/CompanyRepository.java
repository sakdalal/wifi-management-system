package com.sak.wifi.repository;

import com.sak.wifi.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {

    boolean existsByEmail(String email);
}
