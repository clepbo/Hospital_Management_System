package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IPatientProfilePictureService {
    ResponseEntity<CustomResponse> addProfilePicture(Long patientId, MultipartFile file) throws IOException;
    ResponseEntity<CustomResponse> getProfilePictureById(Long id);
    ResponseEntity<CustomResponse> getProfilePictureByPatientId(Long patientId);
    ResponseEntity<CustomResponse> deleteProfilePicture(Long id);
}
