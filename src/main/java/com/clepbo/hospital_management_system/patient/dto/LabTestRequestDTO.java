package com.clepbo.hospital_management_system.patient.dto;

import com.clepbo.hospital_management_system.appointment.entity.Status;

import java.time.LocalDate;

public record LabTestRequestDTO(
        String testName,
        String description,
        String testResult,
        String recommendations,
        Long patientId,
        Long staffId,
        String testStatus,
        LocalDate testDate
) {
}
