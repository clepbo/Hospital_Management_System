package com.clepbo.hospital_management_system.patient.dto;

import lombok.Builder;

@Builder
public record PatientBioRequestDTO(
        String firstname,
        String lastname,
        String email,
        String gender,
        String dateOfBirth,
        String phoneNumber) {
}
