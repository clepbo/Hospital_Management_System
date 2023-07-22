package com.clepbo.hospital_management_system.patient.controller;

import com.clepbo.hospital_management_system.patient.dto.RequestToSeeADoctorRequestDTO;
import com.clepbo.hospital_management_system.patient.service.IRequestToSeeADoctorService;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/seeADoctor")
@RequiredArgsConstructor
@Tag(name = "Request To See A Doctor", description = "Hospital Management System Patient Requests To See A Doctor")
public class RequestToSeeADoctorController {


    private final IRequestToSeeADoctorService seeADoctorService;
    private static final String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @PostMapping("/createRequest/{patientId}")
    @Operation(summary = "Create a Request to See a Doctor", description = "Provide the patient's uniques Id or email to allow the patient create a request to see a doctor", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<CustomResponse> createARequest(@PathVariable("patientId") String patientId, @RequestBody RequestToSeeADoctorRequestDTO requestDTO) throws UnsupportedEncodingException {
        if(Pattern.matches(emailRegex, patientId)){
            String encodedEmail = URLEncoder.encode(patientId, "UTF-8");
            return seeADoctorService.createARequest(encodedEmail, requestDTO);
        }else{
            return seeADoctorService.createARequest(patientId, requestDTO);
        }
    }

    @GetMapping
    @Operation(summary = "Get All Request", description = "Fetch all requests made to see a doctor", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DOCTOR', 'ROLE_RECEPTIONIST')")
    public ResponseEntity<CustomResponse> getAllRequest(){
        return seeADoctorService.getAllRequest();
    }

    @Operation(summary = "Get request by Id", description = "Provide the request unique Id to view the request", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DOCTOR', 'ROLE_RECEPTIONIST')")
    @GetMapping("/request/{requestId}")
    public ResponseEntity<CustomResponse> getRequestById(@PathVariable("requestId") Long requestId){
        return seeADoctorService.viewRequestById(requestId);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "View Patient's Requests", description = "Provide the patient's uniques Id or email to patient's requests", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'ROLE_RECEPTIONIST', 'ROLE_ADMIN')")
    public ResponseEntity<CustomResponse> getRequestByPatientId(@PathVariable("patientId") String patientId) throws UnsupportedEncodingException {
        if(Pattern.matches(emailRegex, patientId)){
            String encodedEmail = URLEncoder.encode(patientId, "UTF-8");
            return seeADoctorService.viewRequestByPatientId(encodedEmail);
        }else{
            return seeADoctorService.viewRequestByPatientId(patientId);
        }
    }

    @GetMapping("/requestStatus/{status}")
    @Operation(summary = "Get request by status", description = "Provide a request status to view requests with such status", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('ROLE_PATIENT', 'ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<CustomResponse> getRequestByStatus(@PathVariable("status") String status){
        return seeADoctorService.viewRequestByStatus(status);
    }

    @PutMapping("/request/{requestId}")
    public ResponseEntity<CustomResponse> updateRequestStatus(@PathVariable("requestId") Long requestId, @RequestBody RequestToSeeADoctorRequestDTO requestDTO, @RequestParam String status){
        return seeADoctorService.updateRequestStatus(requestId, requestDTO, status);
    }

    @DeleteMapping("/request/{requestId}")
    @Operation(summary = "Delete request by Id", description = "Provide the request unique Id to delete the request", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN')")
    public ResponseEntity<CustomResponse> deleteRequestById(@PathVariable("requestId") Long requestId){
        return seeADoctorService.deleteRequest(requestId);
    }

    @DeleteMapping("/patien/{patientId}")
    @Operation(summary = "Delete all patient's request", description = "Provide the patient's unique Id to delete all patient's request", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN')")
    public ResponseEntity<CustomResponse> deleteRequestByPatientId(@PathVariable("patientId") Long patientId){
        return seeADoctorService.deleteAllPatientRequest(patientId);
    }
}
