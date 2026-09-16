package com.sak.wifi.dto;

import com.sak.wifi.entity.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long companyId;
    private Role role;
}
