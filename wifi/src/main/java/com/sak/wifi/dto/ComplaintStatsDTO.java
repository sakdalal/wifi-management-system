package com.sak.wifi.dto;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintStatsDTO {

    private Long open;
    private Long assigned;
    private Long resolved;
    private Long inProgress;
}
