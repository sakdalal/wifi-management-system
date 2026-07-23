package com.sak.wifi.dto;

import com.sak.wifi.entity.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {

    private Long id;

    private String name;

    private String email;

    private String address;

    private String phone;

    private CustomerStatus status;

    private LocalDate installationDate;

    private Long companyId;

}
