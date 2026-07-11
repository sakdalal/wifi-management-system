package com.sak.wifi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(unique = true,nullable = false)
    private String email;

    private String phone;
    private String address;

}
