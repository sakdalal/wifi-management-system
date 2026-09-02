package com.sak.wifi.dto;

import com.sak.wifi.entity.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String address;

    private CustomerStatus status;

    private String profileImageUrl;

    private Long companyId;

    private  Long planId;

    private String currentPlan;

    private Integer speed;

    private BigDecimal price;

}
