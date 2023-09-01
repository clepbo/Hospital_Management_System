package com.clepbo.hospital_management_system.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffLoginRequestDTO {
    private String email;
    private String password;
}
