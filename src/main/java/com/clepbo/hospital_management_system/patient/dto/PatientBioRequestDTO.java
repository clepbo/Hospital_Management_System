package com.clepbo.hospital_management_system.patient.dto;

public record PatientBioRequestDTO(String firstname, String lastname, String email, String gender, String dateOfBirth, String phoneNumber) {
}