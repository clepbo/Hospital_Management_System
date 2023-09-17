package com.clepbo.hospital_management_system.staff.controller;

import com.clepbo.hospital_management_system.staff.dto.*;
import com.clepbo.hospital_management_system.staff.service.IStaffProfilePictureService;
import com.clepbo.hospital_management_system.staff.service.IStaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("api/v1/staff")
@RequiredArgsConstructor
@Tag(name = "staff", description = "Hospital Management System Staff Module")
public class StaffController {
    private final IStaffService staffService;
    private final IStaffProfilePictureService profilePictureService;

    //Add new staff
    @Operation(summary = "Create a New Staff Entity", description = "Provide necessary information about a staff to add them to the hospital system", tags = { "staff" })
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<CustomResponse> createStaff(@RequestBody StaffRequestDto request){
        return staffService.createNewStaff(request);
    }

    @Operation(summary = "Authenticate staff", description = "Provide the staff email and password to authenticate", tags = { "staff" })
    @PostMapping("/authenticate")
    public ResponseEntity<CustomResponse> authenticateStaff(@RequestBody StaffLoginRequestDTO request){
        return staffService.authenticateStaff(request);
    }

    @Operation(summary = "Fetch All staffs from the database", description = "Fetch All staffs from the database", tags = { "staff" })
    @GetMapping("/allStaff")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<CustomResponse> getAllStaff(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size){
        return staffService.getAllStaff(page, size);
    }

    @Operation(summary = "Get a Staff Record by Id", description = "Provide a unique Id for a staff to fetch their record", tags = { "staff" })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin:read', 'doctor:read', 'receptionist:read')")
    public ResponseEntity<CustomResponse> getStaffById (@PathVariable Long id){
        return staffService.findStaffById(id);
    }

    @Operation(summary = "Update Staff Record/Bio", description = "Provide the staff Unique Id to update the staff record if at all they exist", tags = { "staff" })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<CustomResponse> updateStaff(@PathVariable Long id, @RequestBody StaffBioDataRequestDto requestDto){
        return staffService.updateStaff(requestDto, id);
    }

    @Operation(summary = "Add Contact address for an existing staff", description = "Provide the staff Unique Id to add their address", tags = { "staff" })
    @PostMapping("/address")
    @PreAuthorize("hasAuthority('doctor:create')")
    public ResponseEntity<CustomResponse> addAddress(@RequestParam("staffId") Long staffId, @RequestBody StaffAddressDTO addressDTO){
        return staffService.addStaffAddress(staffId, addressDTO);
    }

    @Operation(summary = "Update Staff Address", description = "Provide the staff unique Id and the address Id to update the staff address", tags = { "staff" })
    @PutMapping("/address/{staffId}")
    @PreAuthorize("hasAuthority('doctor:update')")
    public ResponseEntity<CustomResponse> updateStaffAddress(@PathVariable("staffId") Long staffId, @RequestParam Long addressId, @RequestBody StaffAddressDTO addressDTO){
        return staffService.updateStaffAddress(staffId, addressDTO, addressId);
    }

    @Operation(summary = "View/Fetch staff address(es)", description = "Provide the staff unique Id to view all the staff contact address(es)", tags = { "staff" })
    @GetMapping("/address/{staffId}")
    @PreAuthorize("hasAnyAuthority('admin:read', 'doctor:read', 'receptionist:read')")
    public ResponseEntity<CustomResponse> getStaffAdressByStaffId(@PathVariable("staffId") Long staffId){
        return staffService.getStaffAddressByStaffId(staffId);
    }

    @Operation(summary = "Delete all address associated to a staff by the staffId", description = "Provide the staff unique Id to delete all address by the staff", tags = { "staff" })
    @DeleteMapping("/staffAddress/{staffId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<CustomResponse> deleteAddressByStaffId(@PathVariable("staffId") Long staffId){
        return staffService.deleteStaffAddressByStaffId(staffId);
    }

    @Operation(summary = "Delete a single staff Address", description = "Provide the addressId to delete a particular staff address", tags = { "staff" })
    @DeleteMapping("/address/{addressId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<CustomResponse> deleteAddressByAddressId(@PathVariable("addressId") Long addressId){
        return staffService.deleteStaffAddress(addressId);
    }

    @Operation(summary = "Delete a staff from the record", description = "Provide the staff Unique Id to delete the staff's record and all that is associated with it from the Hospital Management system", tags = { "staff" })
    @DeleteMapping("/deleteStaff/{staffId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<CustomResponse> deleteStaffById(@PathVariable("staffId") Long staffId){
        return staffService.deleteStaff(staffId);
    }

    @Operation(summary = "Upload staff's profile picture", description = "Upload staff's profile picture", tags = { "staff" })
    @PostMapping("/profilePicture/upload/{staffId}")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<CustomResponse> uploadProfilePicture(@PathVariable("staffId") Long staffId, @RequestBody MultipartFile file) throws IOException {
        return profilePictureService.addProfilePicture(staffId, file);
    }

}
