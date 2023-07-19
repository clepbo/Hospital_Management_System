package com.clepbo.hospital_management_system.patient.dto;

import com.clepbo.hospital_management_system.appointment.entity.Appointment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientMedicalHistoryResponseDTO {
    private Long id;
    private String symptoms;
    private String complaints;
    private String prescribedMedication;
    private List<Appointment> appointment;
}
