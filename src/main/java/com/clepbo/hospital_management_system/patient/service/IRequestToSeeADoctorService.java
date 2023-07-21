package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.patient.dto.RequestToSeeADoctorRequestDTO;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;

public interface IRequestToSeeADoctorService {
    ResponseEntity<CustomResponse> createARequest(String patientId, RequestToSeeADoctorRequestDTO requestDTO) throws UnsupportedEncodingException;
    ResponseEntity<CustomResponse> getAllRequest();
    ResponseEntity<CustomResponse> viewRequestById(Long requestId);
    ResponseEntity<CustomResponse> viewRequestByPatientId(String patientId) throws UnsupportedEncodingException;
    ResponseEntity<CustomResponse> viewRequestByStatus(String status);
    ResponseEntity<CustomResponse> deleteRequest(Long id);
    ResponseEntity<CustomResponse> deleteAllPatientRequest(Long patientId);
}
