package com.sak.wifi.Controller;

import com.sak.wifi.dto.CustomerRequestDTO;
import com.sak.wifi.dto.CustomerResponseDTO;
import com.sak.wifi.dto.PageResponseDTO;
import com.sak.wifi.entity.CustomerStatus;
import com.sak.wifi.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(
        name = "Customers",
        description = "APIs for managing customers"
)
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(
            summary = "Create Customer",
            description = "Creates a new customer for the current company"
    )
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Valid
            @RequestBody CustomerRequestDTO request
            ){
            return ResponseEntity.ok(
                    customerService.createCustomer(request)
            );
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get Customer using Id",
            description = "Gets customer for the current Id"
    )
    public  ResponseEntity<CustomerResponseDTO> getCustomer(
            @PathVariable Long id
    ){
            return ResponseEntity.ok(
                    customerService.getCustomer(id)
            );
    }


    @GetMapping
    @Operation(
            summary = "Get All Customer ",
            description = "Gets All Customers for the current company"
    )
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
    @Operation(
            summary = "Delete Customer using Id",
            description = "delete customer for the current Id"
    )
    public ResponseEntity<String> deleteCustomer(
            @PathVariable Long id){
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted");
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Customer using Id",
            description = "updates customer for the current Id"
    )
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @Valid
            @RequestBody CustomerRequestDTO request
    ){
        return ResponseEntity.ok(customerService.updateCustomer(id,request));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search Customer",
            description = "search customer for the keyword in parameters"
    )
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomer(
            @RequestParam String keyword
    ){
        return ResponseEntity.ok(
                customerService.searchCustomers(keyword)
        );
    }

    @GetMapping("/find")
    @Operation(
            summary = "Find Customer using Status",
            description = "find customer for the status,if present in parameters"
    )
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
    @Operation(
            summary = "Upload image",
            description = "uploads image for the id present in the parameters " +
                    "for the file mentioned in request body"
    )
    public CustomerResponseDTO uploadImage(
            @PathVariable Long id,
            @RequestParam("file")MultipartFile file
            ){

        return customerService.uploadImage(id,file);
    }


    @PutMapping("/{customerId}/assign-plan/{planId}")
    @Operation(
            summary = "Assign Plan",
            description = "assign plan for the customer with id mentioned to " +
                    "the plan with id mentioned in parameters"
    )
    public ResponseEntity<CustomerResponseDTO> assignPlan(
            @PathVariable Long customerId,
            @PathVariable Long planId
    ){
        return ResponseEntity.ok(customerService.assignPlan(customerId,planId));

    }

    @PutMapping("/{customerId}/upgrade-plan/{planId}")
    @Operation(
            summary = "Upgrade Plan",
            description = "upgrade plan for the customer with id mentioned " +
                    "to the plan with id mentioned in parameters"
    )
    public ResponseEntity<CustomerResponseDTO> upgradePlan(
            @PathVariable Long customerId,
            @PathVariable Long planId
    ){
        return ResponseEntity.ok(customerService.upgradePlan(customerId,planId));

    }

    @PutMapping("/{customerId}/downgrade-plan/{planId}")
    @Operation(
            summary = "Downgrade Plan",
            description = "downgrade plan for the customer with id mentioned " +
                    "to the plan with id mentioned in parameters"
    )
    public ResponseEntity<CustomerResponseDTO> downgradePlan(
            @PathVariable Long customerId,
            @PathVariable Long planId
    ){
        return ResponseEntity.ok(customerService.downgradePlan(customerId,planId));

    }

}
