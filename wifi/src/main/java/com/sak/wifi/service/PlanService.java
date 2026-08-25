package com.sak.wifi.service;

import com.sak.wifi.config.TenantContext;
import com.sak.wifi.dto.PlanRequestDTO;
import com.sak.wifi.entity.Company;
import com.sak.wifi.entity.Plan;
import com.sak.wifi.entity.PlanStatus;
import com.sak.wifi.repository.CompanyRepository;
import com.sak.wifi.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final CompanyRepository companyRepository;

    public Plan createPlan(PlanRequestDTO request){
        Long companyId= TenantContext.getCompanyId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if(planRepository.findByPlanNameAndCompanyId(request.getPlanName(),companyId).isPresent()){
            throw new RuntimeException("Plan already exists for this company");
        }
        if(request.getPrice().compareTo(BigDecimal.ZERO)<0){
            throw new RuntimeException("Price cannot be negative");
        }
        Plan plan = new Plan();

        plan.setPlanName(request.getPlanName());
        plan.setSpeedMbps(request.getSpeedMbps());
        plan.setPrice(request.getPrice());
        plan.setValidityDays(request.getValidityDays());
        plan.setDescription(request.getDescription());
        plan.setActive(request.getActive());
        plan.setCompany(company);
        plan.setActive(request.getActive());
        plan.setPlanStatus(PlanStatus.ACTIVE);
        return planRepository.save(plan);

    }

    public Plan updatePlan(Long id, PlanRequestDTO updated){
        Long companyId= TenantContext.getCompanyId();

        Plan plan= planRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(()->new RuntimeException("No such plan exists"));

        plan.setPlanName(updated.getPlanName());
        plan.setSpeedMbps(updated.getSpeedMbps());
        plan.setPrice(updated.getPrice());
        plan.setDescription(updated.getDescription());
        plan.setValidityDays(updated.getValidityDays());
        plan.setActive(updated.getActive());
        plan.setPlanStatus(updated.getPlanStatus());

        return planRepository.save(plan);

    }

    public List<Plan> getAllPlans(){
        Long companyId= TenantContext.getCompanyId();

        return planRepository.findByCompanyId(companyId);
    }

    public Plan getPlanById(Long id){
        Long companyId= TenantContext.getCompanyId();
        return planRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(()->new RuntimeException("No plan exists with id: "+id));

    }

    public String deletePlan(Long id){
        Long companyId= TenantContext.getCompanyId();

        Plan plan= planRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(()->new RuntimeException("No plan to delete with id: "+id));
        planRepository.delete(plan);

        return "Plan deleted successfully";
    }




}
