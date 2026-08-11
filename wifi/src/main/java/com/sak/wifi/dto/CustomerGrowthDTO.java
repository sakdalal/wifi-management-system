package com.sak.wifi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerGrowthDTO {

    private String month;
    private Long customerCount;
}
