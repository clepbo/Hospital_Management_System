package com.clepbo.hospital_management_system.staff.dto;

public record StaffBioDataRequestDto(String firstName, String lastName, String email, String gender, String dateOfBirth, String phoneNumber, String roles, String status, Double salary) {
}
