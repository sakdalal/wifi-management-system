package com.sak.wifi.dto;

import com.sak.wifi.entity.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Builder
public class CustomerResponseDTO {

    private Long id;

    private String name;

    private String email;

    private String address;

    private String phone;

    private CustomerStatus status;

    private LocalDate installationDate;

    private String profileImageUrl;

    private Long companyId;
}
