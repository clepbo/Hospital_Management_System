package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.patient.dto.LabTestRequestDTO;
import com.clepbo.hospital_management_system.patient.entity.LabTest;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface ILabTestService {
    ResponseEntity<CustomResponse> createNewLabTest(LabTestRequestDTO requestDTO);
    ResponseEntity<CustomResponse> getAllLabTest(int page, int size);
    ResponseEntity<CustomResponse> findTestById(Long id);
    ResponseEntity<CustomResponse> findTestByTestName(String testName, int page, int size);
    ResponseEntity<CustomResponse> findTestByPatientId(Long patientId, int page, int size);
    ResponseEntity<CustomResponse> findTestByStatus(Status status, int page, int size);
    ResponseEntity<CustomResponse> findTestByStaffId(Long staffId, int page, int size);
    ResponseEntity<CustomResponse> findTestByDate(LocalDate testDate, int page, int size);
    ResponseEntity<CustomResponse> updateTest(Long id, LabTestRequestDTO requestDTO);
    ResponseEntity<CustomResponse> updateTestStatus(Long id, String status);
    ResponseEntity<CustomResponse> deleteTest(Long id);

}
