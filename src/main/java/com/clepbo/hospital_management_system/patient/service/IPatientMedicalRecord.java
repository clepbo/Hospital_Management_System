package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.patient.dto.PatientMedicalHistoryRequestDTO;
import com.clepbo.hospital_management_system.patient.dto.PatientMedicalRecordRequestDTO;
import com.clepbo.hospital_management_system.patient.entity.PatientMedicalRecord;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

public interface IPatientMedicalRecord {
    ResponseEntity<CustomResponse> createPatientMedicalRecord(PatientMedicalRecordRequestDTO medicalRecordRequestDTO);
    ResponseEntity<CustomResponse> getAllPatientMedicalRecord();
    ResponseEntity<CustomResponse> findPatientMedicalRecord(Long patientId);
    ResponseEntity<CustomResponse> updatePatientMedicalRecord(PatientMedicalRecordRequestDTO medicalRecordRequestDTO, Long patientId);
    ResponseEntity<CustomResponse> deletePatientMedicalRecord(Long recordId);
    ResponseEntity<CustomResponse> deleteAllPatientMedicalRecord(Long patientId);
}
