package com.clepbo.hospital_management_system.patient.dto;

public record PatientMedicalRecordRequestDTO(
        String patientId,
        String bloodGroup,
        String genotype,
        String allergies,
        String bloodPressure,
        String heartRate,
        String respiratoryRate,
        String terminalIllness) {
}
