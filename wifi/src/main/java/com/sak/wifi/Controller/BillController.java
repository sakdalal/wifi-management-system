package com.sak.wifi.Controller;

import com.sak.wifi.dto.BillResponseDTO;
import com.sak.wifi.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping("/generate/{customerId}")
    public ResponseEntity<BillResponseDTO> generateBill(
            @PathVariable Long customerId
    ){
        return ResponseEntity.ok(billService.generateBill(customerId));
    }

    @GetMapping
    public ResponseEntity<List<BillResponseDTO>> getAllBills(){
        return ResponseEntity.ok(billService.getAllBills());
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByCustomer(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(billService.getBillsByCustomer(id));
    }

    @PutMapping("/pay/{billId}")
    public ResponseEntity<BillResponseDTO> payBill(
            @PathVariable Long billId
    ){
        return ResponseEntity.ok(billService.payBill(billId));
    }

}
