package com.clepbo.hospital_management_system.patient.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestToSeeADoctorResponseDTO {
    private Long id;
    private String patientName;
    private String patientEmail;
    private String reason;
    private String status;
    private Timestamp dateRequested;
}
