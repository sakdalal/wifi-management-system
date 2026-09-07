package com.sak.wifi.Controller;

import com.sak.wifi.dto.PaymentRequestDTO;
import com.sak.wifi.dto.PaymentResponseDTO;
import com.sak.wifi.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments(){
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPayment(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @PostMapping("/bill/{billId}")
    public ResponseEntity<PaymentResponseDTO> payBill(
            @PathVariable Long billId,
            @RequestBody PaymentRequestDTO request
    ){
        return ResponseEntity.ok(paymentService.payBill(billId,request));
    }

}
