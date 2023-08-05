package com.clepbo.hospital_management_system.patient.dto;

import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabTestResponseDTO {
    private Long id;
    private String testName;
    private String description;
    private String testResult;
    private String recommendations;
    private String patientName;
    private String carriedOutBy;
    private LocalDate testDate;
    private String testStatus;
}
