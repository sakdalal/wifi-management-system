package com.sak.wifi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private LocalDate billingDate;

    private String billingMonth;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

}
