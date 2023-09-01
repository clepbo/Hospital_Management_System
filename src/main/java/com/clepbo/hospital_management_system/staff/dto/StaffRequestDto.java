package com.clepbo.hospital_management_system.staff.dto;

import com.clepbo.hospital_management_system.staff.entity.Roles;
import lombok.Builder;

@Builder
public record StaffRequestDto(String firstName, String lastName, String email, String password, Roles roles) {
}
