package com.sak.wifi.service;

import com.sak.wifi.config.TenantContext;
import com.sak.wifi.dto.AssignEmployeeRequestDTO;
import com.sak.wifi.dto.ComplaintRequestDTO;
import com.sak.wifi.dto.ComplaintResponseDTO;
import com.sak.wifi.dto.UpdateComplaintStatusDTO;
import com.sak.wifi.entity.*;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.ComplaintRepository;
import com.sak.wifi.repository.CustomerRepository;
import com.sak.wifi.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final ActivityLogService activityLogService;

    private static final Logger logger= LoggerFactory.getLogger(ComplaintService.class);

    @CacheEvict(
            value = "dashboard",
            key="T(com.sak.wifi.config.TenantContext).getCompanyId()"
    )
    public ComplaintResponseDTO createComplaint(ComplaintRequestDTO request){

        Long companyId = TenantContext.getCompanyId();

        Customer customer= customerRepository.findByIdAndCompanyId(request.getCustomerId(),companyId)
                .orElseThrow(()-> new ResourceNotFoundException("Customer not find with id:" + request.getCustomerId()));

        Complaint complaint=new Complaint();

        complaint.setCustomer(customer);
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint.setPriority(request.getPriority());
        complaint.setCompany(customer.getCompany());

        logger.info("Creating complaint for customer {}", customer.getId());


        Complaint savedComplaint=complaintRepository.save(complaint);
        ComplaintResponseDTO response= modelMapper.map(savedComplaint,ComplaintResponseDTO.class);

        response.setCustomerName(customer.getName());
        response.setCustomerId(customer.getId());


        notificationService.createNotification("New Complaint",
                "Complaint #"+savedComplaint.getId()+ "created",
                "ADMIN",
                1L);

        emailService.sendEmail(customer.getEmail(),
                "Complaint Registered",
                "Your complaint has been registered successfully");

        logger.info("Complaint {} created successfully", complaint.getId());
        activityLogService.logActivity("Complaint created",
                "Complaint "+complaint.getId()+" created",
                "Customer");
        return response;

    }

    public List<ComplaintResponseDTO> getAllComplaints(){
        Long companyId= TenantContext.getCompanyId();

        List<Complaint> complaints=complaintRepository.findByCompanyId(companyId);
        return complaints.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public ComplaintResponseDTO getComplaintById(Long id){

        Long companyId= TenantContext.getCompanyId();
        Complaint complaint=complaintRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(()->new ResourceNotFoundException("complaint not found with id:"+id));
        return convertToDTO(complaint);
    }

    @CacheEvict(
            value = "dashboard",
            key="T(com.sak.wifi.config.TenantContext).getCompanyId()"
    )
    public ComplaintResponseDTO updateComplaint(Long id,ComplaintRequestDTO request){

        Long companyId= TenantContext.getCompanyId();


        Complaint complaint=complaintRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(()->new ResourceNotFoundException("complaint not found with id:"+id));

        Customer customer=customerRepository.findByIdAndCompanyId(request.getCustomerId(),companyId)
                .orElseThrow(()->new ResourceNotFoundException("No Customer not find"));

        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setPriority(request.getPriority());
        complaint.setCustomer(customer);
        Complaint updatedComplaint= complaintRepository.save(complaint);

        return convertToDTO(updatedComplaint);

    }


    @CacheEvict(
            value = "dashboard",
            key="T(com.sak.wifi.config.TenantContext).getCompanyId()"
    )
    public void deleteComplaint(Long id){
        Long companyId= TenantContext.getCompanyId();

        Complaint complaint= complaintRepository.findByIdAndCompanyId(id,companyId)
                .orElseThrow(()->new ResourceNotFoundException("Complaint not found with id:"+id));

        complaintRepository.delete(complaint);
    }

    @Transactional
    @CacheEvict(
            value = "dashboard",
            key="T(com.sak.wifi.config.TenantContext).getCompanyId()"
    )
    public ComplaintResponseDTO assignEmployee(Long complainId,
                                               AssignEmployeeRequestDTO request){

        Long companyId= TenantContext.getCompanyId();


        Complaint complaint=complaintRepository.findByIdAndCompanyId(complainId,companyId)
                .orElseThrow(()->new ResourceNotFoundException("No such complaint exist"));

        Employee employee= employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(()->new ResourceNotFoundException("No such Employee found"));

        complaint.setAssignedEmployee(employee);
        complaint.setStatus(ComplaintStatus.ASSIGNED);

        Complaint saved=complaintRepository.save(complaint);

        notificationService.createNotification("Complaint Assigned",
                "Complaint #"+saved.getId()+ "assigned",
                "EMPLOYEE",
                employee.getId());

        emailService.sendEmail(employee.getEmail(),
                "Complaint Assigned",
                "A complaint has been assigned to you");

        Customer customer=saved.getCustomer();

        emailService.sendEmail(customer.getEmail(),
                "Complaint Assigned",
                "Your complaint #" +saved.getId()+" has been assigned to our support team");

        logger.info("Assigning complaint {} to employee {}", complaint.getId(), employee.getId());
        activityLogService.logActivity(
                "Complaint Assigned",
                "Complaint " + complaint.getId()
                        + " assigned to employee "
                        + employee.getId(),
                "Admin");

        return convertToDTO(saved);
    }

    @Transactional
    @CacheEvict(
            value = "dashboard",
            key="T(com.sak.wifi.config.TenantContext).getCompanyId()"
    )
    public ComplaintResponseDTO updateStatus(Long complainId,
                                               UpdateComplaintStatusDTO request){

        Long companyId =TenantContext.getCompanyId();

        Complaint complaint=complaintRepository.findByIdAndCompanyId(complainId,companyId)
                .orElseThrow(()->new ResourceNotFoundException("No such complaint exist"));

        complaint.setStatus(request.getStatus());

        Complaint saved=complaintRepository.save(complaint);

        if(request.getStatus()==ComplaintStatus.RESOLVED){
            notificationService.createNotification("Complaint Resolved",
                    "Complaint #"+saved.getId()+ "resolved",
                    "CUSTOMER",
                    complaint.getCustomer().getId());
            emailService.sendEmail(complaint.getCustomer().getEmail(),
                    "Complaint Resolved",
                    "Your complaint #"+saved.getId()+ " has been resolved");
            activityLogService.logActivity(
                    "Complaint Closed",
                    "Complaint " + complaint.getId() + " resolved",
                    "Employee");

        } else if(request.getStatus()==ComplaintStatus.IN_PROGRESS){
            notificationService.createNotification("Complaint In-Progress",
                    "Complaint #"+saved.getId()+ "in progress",
                    "CUSTOMER",
                    complaint.getCustomer().getId());
            emailService.sendEmail(complaint.getCustomer().getEmail(),
                    "Complaint In Progress",
                    "Your complaint #"+saved.getId()+ " is still in-progress");
            activityLogService.logActivity(
                    "Complaint In Progress",
                    "Complaint " + complaint.getId() + " in progress",
                    "Employee");
        }

        logger.info("Complaint {} status changed to {}", complaint.getId(), complaint.getStatus());
        return convertToDTO(saved);
    }

    public List<ComplaintResponseDTO> getComplaintByStatus(ComplaintStatus status){

        Long companyId=TenantContext.getCompanyId();

        return complaintRepository.findByCompanyIdAndStatus(companyId,status)
                .stream()
                .map(complaint -> modelMapper.map(complaint, ComplaintResponseDTO.class))
                .toList();
    }

    public Map<String,Long> dashboardCounts(){
        Long companyId= TenantContext.getCompanyId();
        Map<String,Long> counts = new HashMap<>();
        counts.put("open",complaintRepository.countByStatusAndCompanyId(ComplaintStatus.OPEN,companyId));
        counts.put("resolved",complaintRepository.countByStatusAndCompanyId(ComplaintStatus.RESOLVED,companyId));
        counts.put("assigned",complaintRepository.countByStatusAndCompanyId(ComplaintStatus.ASSIGNED,companyId));

        return counts;
    }

    private ComplaintResponseDTO convertToDTO(Complaint complaint){

        ComplaintResponseDTO dto =
                modelMapper.map(complaint,
                        ComplaintResponseDTO.class);

        dto.setCustomerId(
                complaint.getCustomer().getId());

        dto.setCustomerName(
                complaint.getCustomer().getName());

        if(complaint.getAssignedEmployee()!=null) {
            dto.setAssignedEmployeeId(complaint.getAssignedEmployee().getId());
            dto.setAssignedEmployeeName(complaint.getAssignedEmployee().getName());
        }

        return dto;
    }

}
