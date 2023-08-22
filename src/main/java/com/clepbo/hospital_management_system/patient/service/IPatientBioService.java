package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.patient.dto.PatientBioRequestDTO;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

public interface IPatientBioService {
    //Patient Bio Service
    ResponseEntity<CustomResponse> createPatientBio(PatientBioRequestDTO patientBioRequestDTO);
    ResponseEntity<CustomResponse> getAllPatientRecord(int page, int size);
    ResponseEntity<CustomResponse> findPatientBioById(Long id);
    ResponseEntity<CustomResponse> updatePatientBio(PatientBioRequestDTO patientBioRequestDTO, Long id);
    ResponseEntity<CustomResponse> deletePatient(Long id);
}
