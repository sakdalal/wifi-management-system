package com.sak.wifi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopPlanDTO {

    private Long planId;
    private String planName;
    private Long customerCount;
}
