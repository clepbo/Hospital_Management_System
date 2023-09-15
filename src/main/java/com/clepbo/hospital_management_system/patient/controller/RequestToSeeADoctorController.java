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
@RequestMapping("/api/v1/seeADoctor")
@RequiredArgsConstructor
@Tag(name = "Request To See A Doctor", description = "Hospital Management System Patient Requests To See A Doctor")
public class RequestToSeeADoctorController {


    private final IRequestToSeeADoctorService seeADoctorService;
    private static final String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @PostMapping("/createRequest")
    @Operation(summary = "Create a Request to See a Doctor", description = "Provide the patient's uniques Id or email to allow the patient create a request to see a doctor", tags = {"Request To See A Doctor"})
    public ResponseEntity<CustomResponse> createARequest(@RequestBody RequestToSeeADoctorRequestDTO requestDTO) throws UnsupportedEncodingException {
        return seeADoctorService.createARequest(requestDTO);

    }

    @GetMapping
    @Operation(summary = "Get All Request", description = "Fetch all requests made to see a doctor", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('admin:create', 'receptionist:create')")
    public ResponseEntity<CustomResponse> getAllRequest(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size){
        return seeADoctorService.getAllRequest(page, size);
    }

    @Operation(summary = "Get request by Id", description = "Provide the request unique Id to view the request", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('admin:create', 'doctor:create', 'receptionist:create')")
    @GetMapping("/request/{requestId}")
    public ResponseEntity<CustomResponse> getRequestById(@PathVariable("requestId") Long requestId){
        return seeADoctorService.viewRequestById(requestId);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "View Patient's Requests", description = "Provide the patient's uniques Id or email to patient's requests", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('admin:create', 'doctor:create', 'receptionist:create')")
    public ResponseEntity<CustomResponse> getRequestByPatientId(@PathVariable("patientId") String patientId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) throws UnsupportedEncodingException {
        if(Pattern.matches(emailRegex, patientId)){
            String encodedEmail = URLEncoder.encode(patientId, "UTF-8");
            return seeADoctorService.viewRequestByPatientId(encodedEmail, page, size);
        }else{
            return seeADoctorService.viewRequestByPatientId(patientId, page, size);
        }
    }

    @GetMapping("/requestStatus/{status}")
    @Operation(summary = "Get request by status", description = "Provide a request status to view requests with such status", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('admin:create', 'doctor:create', 'receptionist:create')")
    public ResponseEntity<CustomResponse> getRequestByStatus(@PathVariable("status") String status,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size){
        return seeADoctorService.viewRequestByStatus(status, page, size);
    }

//    @PutMapping("/request/{requestId}")
//    @PreAuthorize("hasAnyAuthority('admin:create', 'doctor:create', 'receptionist:create')")
//    public ResponseEntity<CustomResponse> updateRequestStatus(@PathVariable("requestId") Long requestId, @RequestBody RequestToSeeADoctorRequestDTO requestDTO, @RequestParam String status){
//        return seeADoctorService.updateRequestStatus(requestId, requestDTO, status);
//    }

    @DeleteMapping("/request/{requestId}")
    @Operation(summary = "Delete request by Id", description = "Provide the request unique Id to delete the request", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('admin:create', 'doctor:create', 'receptionist:create')")
    public ResponseEntity<CustomResponse> deleteRequestById(@PathVariable("requestId") Long requestId){
        return seeADoctorService.deleteRequest(requestId);
    }

    @DeleteMapping("/patient/{patientId}")
    @Operation(summary = "Delete all patient's request", description = "Provide the patient's unique Id to delete all patient's request", tags = {"Request To See A Doctor"})
    @PreAuthorize("hasAnyAuthority('admin:create', 'doctor:create', 'receptionist:create')")
    public ResponseEntity<CustomResponse> deleteRequestByPatientId(@PathVariable("patientId") Long patientId){
        return seeADoctorService.deleteAllPatientRequest(patientId);
    }
}
