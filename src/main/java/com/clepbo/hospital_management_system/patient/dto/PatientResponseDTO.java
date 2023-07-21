package com.clepbo.hospital_management_system.patient.dto;

import com.clepbo.hospital_management_system.patient.entity.PatientContactAddress;
import com.clepbo.hospital_management_system.patient.entity.PatientMedicalHistory;
import com.clepbo.hospital_management_system.patient.entity.PatientMedicalRecord;
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
public class PatientResponseDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String dateOfBirth;
    private String phoneNumber;
    private String gender;
    private String role;
    private List<PatientContactAddress> contactAddress;
    private List<PatientMedicalHistory> medicalHistory;
    private List<PatientMedicalRecord> medicalRecord;
}
