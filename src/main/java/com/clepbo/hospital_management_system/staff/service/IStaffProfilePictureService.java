package com.clepbo.hospital_management_system.staff.service;

import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import com.clepbo.hospital_management_system.staff.dto.StaffBioDataRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IStaffProfilePictureService {
    ResponseEntity<CustomResponse> addProfilePicture(Long staffId, MultipartFile file) throws IOException;
    ResponseEntity<CustomResponse> getProfilePictureById(Long id);
    ResponseEntity<CustomResponse> deleteProfilePicture(Long id);
}
