package com.sak.wifi.Controller;

import com.sak.wifi.dto.BillResponseDTO;
import com.sak.wifi.dto.PaymentRequestDTO;
import com.sak.wifi.dto.PaymentResponseDTO;
import com.sak.wifi.service.BillService;
import com.sak.wifi.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;
    private final InvoiceService invoiceService;

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
    public ResponseEntity<PaymentResponseDTO> payBill(
            @PathVariable Long billId,
            @RequestBody PaymentRequestDTO request
    ){
        return ResponseEntity.ok(billService.payBill(billId,request));
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(
            @PathVariable Long id
    ){
        byte[] pdf= invoiceService.generateInvoicePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
