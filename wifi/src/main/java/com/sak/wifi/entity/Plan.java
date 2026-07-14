package com.sak.wifi.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name="plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String planName;

    @Column(nullable = false)
    private Integer speedMbps;

    @Column(nullable = false,precision = 10,scale=2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer validityDays;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="company_id")
    private Company company;

    @Column(nullable = false)
    private boolean isActive;
}
