package com.sak.wifi.repository;

import com.sak.wifi.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan,Long> {

    Optional<Plan> findByPlanNameAndCompanyId(String planName,Long companyId);

    List<Plan> findByCompanyId(Long companyId);

    Optional<Plan> findByIdAndCompanyId(
            Long id,
            Long companyId
    );

}
