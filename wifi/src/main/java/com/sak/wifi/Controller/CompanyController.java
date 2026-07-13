package com.sak.wifi.Controller;

import com.sak.wifi.entity.Company;
import com.sak.wifi.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public Company createCompany(@RequestBody Company company){
        return companyService.saveCompany(company);
    }

    @GetMapping
    public List<Company> getCompanies(){
        return companyService.getAllCompanies();
    }

}
