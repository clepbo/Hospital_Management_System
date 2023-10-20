package com.clepbo.hospital_management_system.patient.dto;

import lombok.Builder;

@Builder
public record RequestToSeeADoctorRequestDTO(String patientEmail, String reason) {
}
