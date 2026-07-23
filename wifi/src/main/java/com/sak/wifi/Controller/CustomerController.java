package com.sak.wifi.Controller;

import com.sak.wifi.dto.CustomerRequestDTO;
import com.sak.wifi.dto.CustomerResponseDTO;
import com.sak.wifi.entity.CustomerStatus;
import com.sak.wifi.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.Name;
import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Valid
            @RequestBody CustomerRequestDTO request
            ){
            return ResponseEntity.ok(
                    customerService.createCustomer(request)
            );
    }

    @GetMapping("/{id}")
    public  ResponseEntity<CustomerResponseDTO> getCustomer(
            @PathVariable Long id
    ){
            return ResponseEntity.ok(
                    customerService.getCustomer(id)
            );
    }

    @GetMapping
    public  ResponseEntity<List<CustomerResponseDTO>> getAllCustomers(){
        return ResponseEntity.ok(
                customerService.getAllCustomer()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(
            @PathVariable Long id){
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted");
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @Valid
            @RequestBody CustomerRequestDTO request
    ){
        return ResponseEntity.ok(customerService.updateCustomer(id,request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomer(
            @RequestParam String keyword
    ){
        return ResponseEntity.ok(
                customerService.searchCustomers(keyword)
        );
    }

    @GetMapping("/find")
    public ResponseEntity<List<CustomerResponseDTO>> findCustomers(
            @RequestParam(required = false)
            CustomerStatus status,
            @RequestParam(required = false)
            Long companyId
    ){
        return ResponseEntity.ok(
                customerService.findCustomers(status,companyId)
        );
    }

}
