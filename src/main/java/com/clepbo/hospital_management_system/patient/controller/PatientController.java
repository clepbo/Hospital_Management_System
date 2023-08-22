package com.clepbo.hospital_management_system.patient.controller;

import com.clepbo.hospital_management_system.patient.dto.PatientAddressDTO;
import com.clepbo.hospital_management_system.patient.dto.PatientBioRequestDTO;
import com.clepbo.hospital_management_system.patient.service.IPatientBioService;
import com.clepbo.hospital_management_system.patient.service.IPatientContactAddress;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/patient")
@Tag(name = "patient", description = "Hospital Management System Patient Module")
public class PatientController {
    private final IPatientBioService patientBioService;
    private final IPatientContactAddress contactAddress;

    @Operation(summary = "Create a New Patient File", description = "Provide necessary information about a patient to open a file for them", tags = { "patient" })
    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_RECEPTIONIST')")
    public ResponseEntity<CustomResponse> createPatientBio(@RequestBody PatientBioRequestDTO requestDTO){
        return patientBioService.createPatientBio(requestDTO);
    }

    @Operation(summary = "Fetch/Read a Patient Bio using PatientID", description = "Provide the patient unique Id to read/fetch the patient Bio", tags = { "patient" })
    @GetMapping("/{patientId}")
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_ASSIGNED_DOCTOR')")
    public ResponseEntity<CustomResponse> findPatientBioById(@PathVariable("patientId") Long id){
        return patientBioService.findPatientBioById(id);
    }

    @Operation(summary = "Get all Patient Bio", description = "Get all Patient Bios", tags = { "patient" })
    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN')")
    public ResponseEntity<CustomResponse> getAllPatient(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size){
        return patientBioService.getAllPatientRecord(page, size);
    }

    @Operation(summary = "Update Patient Bio", description = "Provide the patient Unique Id to Update Patient Bio", tags = { "patient" })
    @PutMapping("/{patientId}")
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN')")
    public ResponseEntity<CustomResponse> updatePatientBio(@PathVariable("patientId") Long id, @RequestBody PatientBioRequestDTO requestDTO){
        return patientBioService.updatePatientBio(requestDTO, id);
    }

    @Operation(summary = "Add a contact address to the patient Bio", description = "Provide necessary information about a patient contact address to add it to the database", tags = { "patient" })
    @PostMapping("/address{patientId}")
    @PreAuthorize("hasAuthority('ROLE_RECEPTIONIST')")
    public ResponseEntity<CustomResponse> addPatientContactAddress(@PathVariable("patientId") Long patientId, @RequestBody PatientAddressDTO addressDTO){
        return contactAddress.addPatientAddress(patientId, addressDTO);
    }

    @Operation(summary = "Read all patient's conatc address", description = "Provide a patients uniques Id to Read all their contact address", tags = { "patient" })
    @GetMapping("/address/{patientId}")
    @PreAuthorize("hasAuthority('ROLE_RECEPTIONIST')")
    public ResponseEntity<CustomResponse> getAllPatientAddress(@PathVariable("patientId") Long patientId){
        return contactAddress.findAddressByPatientId(patientId);
    }

    @Operation(summary = "Update Patient's contact address", description = "Provide the patient's uniques Id and the address unique Id to update the patient's contact address", tags = { "patient" })
    @PutMapping("/address/edit/{patientid}")
    @PreAuthorize("hasAuthority('ROLE_RECEPTIONIST')")
    public ResponseEntity<CustomResponse> updatePatientAddress(@PathVariable("patientId") Long patientId, @RequestBody PatientAddressDTO addressDTO, @RequestParam Long addressId){
        return contactAddress.updatePatientAddress(addressDTO, patientId, addressId);
    }

    @Operation(summary = "Delete a Patient's Contact Address", description = "Provide an address unique Id to delete the address", tags = { "patient" })
    @DeleteMapping("/address/{addressId}")
    @PreAuthorize("hasAuthority('ROLE_RECEPTIONIST')")
    public ResponseEntity<CustomResponse> deleteContactAddress(@PathVariable("addressId") Long addressId){
        return contactAddress.deletePatientAddressByAddressId(addressId);
    }

    @Operation(summary = "Delete all Patient's contact address", description = "Provide the patient's uniques Id to delete patient's Contact address", tags = { "patient" })
    @DeleteMapping("/deletePatientAddress/{patientId}")
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<CustomResponse> deleteALlPatientAddress(@PathVariable("patientId") Long patientId){
        return contactAddress.deleteAllPatientAddressByPatientId(patientId);
    }
}
