package com.sak.wifi.service;

import com.sak.wifi.dto.ComplaintRequestDTO;
import com.sak.wifi.dto.ComplaintResponseDTO;
import com.sak.wifi.entity.Complaint;
import com.sak.wifi.entity.ComplaintStatus;
import com.sak.wifi.entity.Customer;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.ComplaintRepository;
import com.sak.wifi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    public ComplaintResponseDTO createComplaint(ComplaintRequestDTO request){

        Customer customer= customerRepository.findById(request.getCustomerId())
                .orElseThrow(()-> new ResourceNotFoundException("Customer not find with id:" + request.getCustomerId()));

        Complaint complaint=new Complaint();



        complaint.setCustomer(customer);
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint.setPriority(request.getPriority());
        complaint.setAssignedEmployee(request.getAssignedEmployee());
        System.out.println("Complaint ID = " + complaint.getId());
        System.out.println("Customer ID = " + complaint.getCustomer().getId());
        System.out.println("Company = " + complaint.getCompany());

        Complaint savedComplaint=complaintRepository.save(complaint);
        ComplaintResponseDTO response= modelMapper.map(savedComplaint,ComplaintResponseDTO.class);
        response.setCustomerName(customer.getName());
        response.setCustomerId(customer.getId());
        complaint.setCompany(customer.getCompany());
        System.out.println("Complaint ID = " + complaint.getId());
        System.out.println("Customer ID = " + complaint.getCustomer().getId());
        System.out.println("Company = " + complaint.getCompany());

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
        complaint.setAssignedEmployee(request.getAssignedEmployee());
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

}
