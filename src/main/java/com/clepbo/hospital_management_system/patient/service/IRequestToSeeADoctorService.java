package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.patient.dto.RequestToSeeADoctorRequestDTO;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;

public interface IRequestToSeeADoctorService {
    ResponseEntity<CustomResponse> createARequest(RequestToSeeADoctorRequestDTO requestDTO) throws UnsupportedEncodingException;
    ResponseEntity<CustomResponse> getAllRequest(int page, int size);
    ResponseEntity<CustomResponse> viewRequestById(Long requestId);
    ResponseEntity<CustomResponse> viewRequestByPatientId(String patientId, int page, int size) throws UnsupportedEncodingException;
    ResponseEntity<CustomResponse> viewRequestByStatus(String status, int page, int size);
    ResponseEntity<CustomResponse> updateRequestStatus(Long requestId, RequestToSeeADoctorRequestDTO requestDTO, String status);
    ResponseEntity<CustomResponse> deleteRequest(Long id);
    ResponseEntity<CustomResponse> deleteAllPatientRequest(Long patientId);
}
