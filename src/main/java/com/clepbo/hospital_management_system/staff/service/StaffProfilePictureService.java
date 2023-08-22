package com.clepbo.hospital_management_system.staff.service;

import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import com.clepbo.hospital_management_system.staff.dto.StaffBioDataRequestDto;
import com.clepbo.hospital_management_system.staff.entity.Staff;
import com.clepbo.hospital_management_system.staff.repository.IStaffProfilePictureRepository;
import com.clepbo.hospital_management_system.staff.repository.IStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffProfilePictureService implements IStaffProfilePictureService{

    private final IStaffProfilePictureRepository repository;
    private final IStaffRepository staffRepository;

    @Override
    public ResponseEntity<CustomResponse> addProfilePicture(Long staffId, MultipartFile file) {
        Optional<Staff> findStaff = staffRepository.findById(staffId);
        if(findStaff.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Staff not found"));
        }
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "File Cannot be empty"));
        }

        String originalFileName = file.getOriginalFilename();
        String fileType = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();
        long fileSize = file.getSize();
        List<String> possibleFileType = Arrays.asList("jpg", "jpeg", "png", "gif");
        if(!possibleFileType.contains(fileType)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "Invalid file type"));
        }
        if(fileSize > 1048576){
            
        }
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> getProfilePictureById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> updateProfilePicture(Long staffId, Long id, MultipartFile file) {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> deleteProfilePicture(Long id) {
        return null;
    }
}
