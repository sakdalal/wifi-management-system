package com.sak.wifi.dto;

import com.sak.wifi.entity.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email")
    private String email;

    @NotBlank
    private String address;

    @Pattern(regexp = "^[0-9]{10}$",
                message = "Phone must be 10 digits")
    private String phone;

    private CustomerStatus status;

    private LocalDate installationDate;

    private String profileImageUrl;

    private Long companyId;
}
