package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.patient.dto.PatientAddressDTO;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

public interface IPatientContactAddress {
    //Patient Contact Address Service
    ResponseEntity<CustomResponse> addPatientAddress(PatientAddressDTO addressDTO);
    ResponseEntity<CustomResponse> findAddressByPatientId(Long patientId);
    ResponseEntity<CustomResponse> findAddressByAddressId(Long addressId);
    ResponseEntity<CustomResponse> updatePatientAddress(PatientAddressDTO patientAddressDTO, Long patientId, Long addressId);
    ResponseEntity<CustomResponse> deletePatientAddressByAddressId(Long addressId);
    ResponseEntity<CustomResponse> deleteAllPatientAddressByPatientId(Long patientId);
}
