package com.sak.wifi.Controller;

import com.sak.wifi.dto.ComplaintRequestDTO;
import com.sak.wifi.dto.ComplaintResponseDTO;
import com.sak.wifi.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
