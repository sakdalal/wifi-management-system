package com.sak.wifi.Controller;

import com.sak.wifi.dto.AssignEmployeeRequestDTO;
import com.sak.wifi.dto.ComplaintRequestDTO;
import com.sak.wifi.dto.ComplaintResponseDTO;
import com.sak.wifi.dto.UpdateComplaintStatusDTO;
import com.sak.wifi.entity.ComplaintStatus;
import com.sak.wifi.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<ComplaintResponseDTO> createComplaint(
            @Valid
            @RequestBody ComplaintRequestDTO requestDTO
            ){
        return new ResponseEntity<>(complaintService.createComplaint(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponseDTO>> getAllComplaints(){
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponseDTO> getComplaintById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplaintResponseDTO> updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintRequestDTO requestDTO
            ){
        return ResponseEntity.ok(complaintService.updateComplaint(id,requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComplaint(
            @PathVariable Long id
    ){
            complaintService.deleteComplaint(id);
            return ResponseEntity.ok("Complaint deleted Successfully");
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<ComplaintResponseDTO> assignEmployee(
            @PathVariable Long id,
            @RequestBody AssignEmployeeRequestDTO requestDTO
            ){

        return ResponseEntity.ok(complaintService.assignEmployee(id,requestDTO));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<ComplaintResponseDTO> updateComplaint(
            @PathVariable Long id,
            @RequestBody UpdateComplaintStatusDTO request
            ){
            return ResponseEntity.ok(complaintService.updateStatus(id,request));
    }

    @GetMapping(params = "status")
    public List<ComplaintResponseDTO> getByStatus(
            @RequestParam ComplaintStatus status
            ){
        return complaintService.getComplaintByStatus(status);

    }

    @GetMapping("/dashboard-counts")
    public Map<String,Long> dashboardCounts(){
        return complaintService.dashboardCounts();
    }

}
