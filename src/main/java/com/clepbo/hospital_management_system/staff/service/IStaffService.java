package com.clepbo.hospital_management_system.staff.service;

import com.clepbo.hospital_management_system.staff.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IStaffService {
    ResponseEntity<CustomResponse> createNewStaff(StaffRequestDto request);
    ResponseEntity<CustomResponse> authenticateStaff(StaffLoginRequestDTO loginRequestDTO);
    ResponseEntity<CustomResponse> getAllStaff(int page, int size);
    ResponseEntity<CustomResponse> findStaffById(Long id);
    ResponseEntity<CustomResponse> updateStaff(StaffBioDataRequestDto requestDto, Long id);
    ResponseEntity<CustomResponse> deleteStaff(Long id);
    ResponseEntity<CustomResponse> addStaffAddress(Long staffId, StaffAddressDTO request);
    ResponseEntity<CustomResponse> getStaffAddressByStaffId(Long staffId);
    ResponseEntity<CustomResponse> updateStaffAddress(Long staffId, StaffAddressDTO addressDTO, Long addressId);
    ResponseEntity<CustomResponse> deleteStaffAddress(Long addressId);
    ResponseEntity<CustomResponse> deleteStaffAddressByStaffId(Long staffId);
}
