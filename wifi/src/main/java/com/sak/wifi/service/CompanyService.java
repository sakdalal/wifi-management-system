package com.sak.wifi.service;

import com.sak.wifi.entity.Company;
import com.sak.wifi.exception.ResourceAlreadyExistsException;
import com.sak.wifi.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Company saveCompany(Company company){
        if(companyRepository.existsByEmail(company.getEmail())){
            throw new ResourceAlreadyExistsException(
                    "Email Already Exists"
            );
        }
        return companyRepository.save(company);
    }

    public List<Company> getAllCompanies(){
        return companyRepository.findAll();
    }

}
