package com.sak.wifi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class UpdateProfileRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;
}
