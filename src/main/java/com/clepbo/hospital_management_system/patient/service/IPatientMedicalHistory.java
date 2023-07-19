package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.patient.dto.PatientMedicalHistoryRequestDTO;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

public interface IPatientMedicalHistory {
    //Patient Medical History
    ResponseEntity<CustomResponse> createPatientMedicalHistory(PatientMedicalHistoryRequestDTO medicalHistoryRequestDTO);
    ResponseEntity<CustomResponse> getAllPatientMedicalHistory();
    ResponseEntity<CustomResponse> findPatientMedicalHistory(Long patientId);
    ResponseEntity<CustomResponse> updatePatientMedicalHistory(PatientMedicalHistoryRequestDTO medicalHistoryRequestDTO, Long patientId);
    ResponseEntity<CustomResponse> deletePatientMedicalHistory(Long historyId);
    ResponseEntity<CustomResponse> deleteAllPatientMedicalHistory(Long patientId);
}
