package com.sak.wifi.dto;

import com.sak.wifi.entity.PaymentMethod;
import com.sak.wifi.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private Long id;

    private BigDecimal amount;

    private LocalDateTime paymentDate;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private String transactionId;

    private Long billId;

    private String customerName;
}
