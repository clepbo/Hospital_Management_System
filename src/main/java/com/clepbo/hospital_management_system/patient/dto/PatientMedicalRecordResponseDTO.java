package com.clepbo.hospital_management_system.patient.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientMedicalRecordResponseDTO {
    private String bloodGroup;
    private String genotype;
    private String allergies;
    private String bloodPressure;
    private String heartRate;
    private String respiratoryRate;
    private String terminalIllness;
}
