package com.sak.wifi.repository;


import com.sak.wifi.entity.Complaint;
import com.sak.wifi.entity.ComplaintPriority;
import com.sak.wifi.entity.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint,Long> {

    long countByStatusAndCompanyId(ComplaintStatus status,Long companyId);

    long countByPriority(ComplaintPriority priority);

    long countByStatusAndCreatedAtBefore(
            ComplaintStatus status,
            LocalDateTime date);

    List<Complaint> findByCompanyId(Long companyId);

    Optional<Complaint> findByIdAndCompanyId(
            Long id,
            Long companyId
    );

    List<Complaint> findByCompanyIdAndStatus(
            Long companyId,
            ComplaintStatus status
    );
}
