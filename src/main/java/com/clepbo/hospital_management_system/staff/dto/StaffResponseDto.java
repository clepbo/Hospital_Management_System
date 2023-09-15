package com.clepbo.hospital_management_system.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String roles;
    private String gender;
    private String dateOfBirth;
    private String phoneNumber;
    private String status;
    private Double salary;
    private boolean isEnabled;
}
