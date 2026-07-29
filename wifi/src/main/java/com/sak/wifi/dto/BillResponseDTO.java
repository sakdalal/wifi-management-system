package com.sak.wifi.dto;

import com.sak.wifi.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillResponseDTO {

    private Long id;
    private BigDecimal amount;
    private LocalDate billingDate;
    private String billingMonth;
    private LocalDate dueDate;
    private String customerName;
    private String planName;
    private PaymentStatus paymentStatus;
}
