package com.sak.wifi.dto;

import com.sak.wifi.entity.PaymentMethod;
import lombok.Data;

@Data
public class PaymentRequestDTO {

    private PaymentMethod paymentMethod;
    private String transactionId;
}
