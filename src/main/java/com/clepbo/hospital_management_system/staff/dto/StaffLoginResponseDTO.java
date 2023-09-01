package com.clepbo.hospital_management_system.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffLoginResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String roles;
    private String accessToken;
    private boolean isEnabled;
}
