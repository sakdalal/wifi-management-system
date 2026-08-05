package com.sak.wifi.service;

import com.sak.wifi.dto.AssignEmployeeRequestDTO;
import com.sak.wifi.dto.ComplaintRequestDTO;
import com.sak.wifi.dto.ComplaintResponseDTO;
import com.sak.wifi.dto.UpdateComplaintStatusDTO;
import com.sak.wifi.entity.Complaint;
import com.sak.wifi.entity.ComplaintStatus;
import com.sak.wifi.entity.Customer;
import com.sak.wifi.entity.Employee;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.ComplaintRepository;
import com.sak.wifi.repository.CustomerRepository;
import com.sak.wifi.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    public ComplaintResponseDTO createComplaint(ComplaintRequestDTO request){

        Customer customer= customerRepository.findById(request.getCustomerId())
                .orElseThrow(()-> new ResourceNotFoundException("Customer not find with id:" + request.getCustomerId()));

        Complaint complaint=new Complaint();



        complaint.setCustomer(customer);
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint.setPriority(request.getPriority());


        Complaint savedComplaint=complaintRepository.save(complaint);
        ComplaintResponseDTO response= modelMapper.map(savedComplaint,ComplaintResponseDTO.class);

        response.setCustomerName(customer.getName());
        response.setCustomerId(customer.getId());
        complaint.setCompany(customer.getCompany());

        notificationService.createNotification("New Complaint",
                "Complaint #"+savedComplaint.getId()+ "created",
                "ADMIN",
                1L);

        return response;

    }

    public List<ComplaintResponseDTO> getAllComplaints(){
        List<Complaint> complaints=complaintRepository.findAll();
        return complaints.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public ComplaintResponseDTO getComplaintById(Long id){
        Complaint complaint=complaintRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("complaint not found with id:"+id));
        return convertToDTO(complaint);
    }

    public ComplaintResponseDTO updateComplaint(Long id,ComplaintRequestDTO request){

        Complaint complaint=complaintRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("complaint not found with id:"+id));

        Customer customer=customerRepository.findById(request.getCustomerId())
                .orElseThrow(()->new ResourceNotFoundException("No Customer not find"));

        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setPriority(request.getPriority());
        complaint.setCustomer(customer);
        Complaint updatedComplaint= complaintRepository.save(complaint);

        return convertToDTO(updatedComplaint);

    }

    public void deleteComplaint(Long id){
        Complaint complaint= complaintRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Complaint not found with id:"+id));

        complaintRepository.delete(complaint);
    }


    private ComplaintResponseDTO convertToDTO(Complaint complaint){

        ComplaintResponseDTO dto =
                modelMapper.map(complaint,
                        ComplaintResponseDTO.class);

        dto.setCustomerId(
                complaint.getCustomer().getId());

        dto.setCustomerName(
                complaint.getCustomer().getName());

        return dto;
    }

    @Transactional
    public ComplaintResponseDTO assignEmployee(Long complainId,
                                               AssignEmployeeRequestDTO request){

        Complaint complaint=complaintRepository.findById(complainId)
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

        return modelMapper.map(saved, ComplaintResponseDTO.class);
    }

    @Transactional
    public ComplaintResponseDTO updateStatus(Long complainId,
                                               UpdateComplaintStatusDTO request){

        Complaint complaint=complaintRepository.findById(complainId)
                .orElseThrow(()->new ResourceNotFoundException("No such complaint exist"));

        complaint.setStatus(request.getStatus());

        Complaint saved=complaintRepository.save(complaint);

        if(request.getStatus()==ComplaintStatus.RESOLVED){
            notificationService.createNotification("Complaint Resolved",
                    "Complaint #"+saved.getId()+ "resolved",
                    "CUSTOMER",
                    complaint.getCustomer().getId());
        } else if(request.getStatus()==ComplaintStatus.IN_PROGRESS){
            notificationService.createNotification("Complaint In-Progress",
                    "Complaint #"+saved.getId()+ "in progress",
                    "CUSTOMER",
                    complaint.getCustomer().getId());
        }

        return modelMapper.map(saved, ComplaintResponseDTO.class);
    }

    public List<ComplaintResponseDTO> getComplaintByStatus(ComplaintStatus status){

        return complaintRepository.findByStatus(status)
                .stream()
                .map(complaint -> modelMapper.map(complaint, ComplaintResponseDTO.class))
                .toList();
    }

    public Map<String,Long> dashboardCounts(){
        Map<String,Long> counts = new HashMap<>();
        counts.put("Open",complaintRepository.countByStatus(ComplaintStatus.OPEN));
        counts.put("Resolved",complaintRepository.countByStatus(ComplaintStatus.RESOLVED));
        counts.put("Assigned",complaintRepository.countByStatus(ComplaintStatus.ASSIGNED));

        return counts;
    }

}
