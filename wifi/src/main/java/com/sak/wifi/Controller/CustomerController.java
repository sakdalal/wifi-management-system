package com.sak.wifi.Controller;

import com.sak.wifi.dto.CustomerRequestDTO;
import com.sak.wifi.dto.CustomerResponseDTO;
import com.sak.wifi.dto.PageResponseDTO;
import com.sak.wifi.entity.CustomerStatus;
import com.sak.wifi.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public  ResponseEntity<PageResponseDTO<CustomerResponseDTO>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        return ResponseEntity.ok(
                customerService.getAllCustomer(page,size,sortBy,direction)
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
            CustomerStatus status
    ){
        return ResponseEntity.ok(
                customerService.findCustomers(status)
        );
    }

    @PostMapping(
            value="/{id}/upload-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CustomerResponseDTO uploadImage(
            @PathVariable Long id,
            @RequestParam("file")MultipartFile file
            ){

        return customerService.uploadImage(id,file);
    }

    @PutMapping("/{customerId}/assign-plan/{planId}")
    public ResponseEntity<CustomerResponseDTO> assignPlan(
            @PathVariable Long customerId,
            @PathVariable Long planId
    ){
        return ResponseEntity.ok(customerService.assignPlan(customerId,planId));

    }

    @PutMapping("/{customerId}/upgrade-plan/{planId}")
    public ResponseEntity<CustomerResponseDTO> upgradePlan(
            @PathVariable Long customerId,
            @PathVariable Long planId
    ){
        return ResponseEntity.ok(customerService.upgradePlan(customerId,planId));

    }

    @PutMapping("/{customerId}/downgrade-plan/{planId}")
    public ResponseEntity<CustomerResponseDTO> downgradePlan(
            @PathVariable Long customerId,
            @PathVariable Long planId
    ){
        return ResponseEntity.ok(customerService.downgradePlan(customerId,planId));

    }

}
