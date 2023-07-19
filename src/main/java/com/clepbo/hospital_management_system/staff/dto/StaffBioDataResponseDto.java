package com.clepbo.hospital_management_system.staff.dto;

import com.clepbo.hospital_management_system.staff.entity.Staff;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffBioDataResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String dateOfBirth;
    private String phoneNumber;
    private String address;
    private String roles;
    private String status;
    private Double salary;
    private Staff staff;
}
