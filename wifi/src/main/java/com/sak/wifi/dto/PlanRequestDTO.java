package com.sak.wifi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PlanRequestDTO {

    private String planName;
    private Integer speedMbps;
    private BigDecimal price;
    private Integer validityDays;
    private String description;
    private Boolean active;
    private Long companyId;

}
