package com.clepbo.hospital_management_system.patient.controller;

import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.patient.dto.LabTestRequestDTO;
import com.clepbo.hospital_management_system.patient.service.ILabTestService;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/labTest")
@Tag(name = "LabTest", description = "Hospital Management System Laboratory Test Module")
public class LabTestController {

    private final ILabTestService labTestService;

    @PostMapping("/newTest")
    @Operation(summary = "Create a New LabTest", description = "Provide necessary information about a patient to open a file for them", tags = { "LabTest" })
    public ResponseEntity<CustomResponse> createNewTest(@RequestBody LabTestRequestDTO requestDTO){
        return labTestService.createNewLabTest(requestDTO);
    }

    @GetMapping("/{testId}")
    @Operation(summary = "Find Test by Id", description = "Provide the test unique Id to view a test", tags = { "LabTest" })
    public ResponseEntity<CustomResponse> findTestById(@PathVariable("testId") Long testId){
        return labTestService.findTestById(testId);
    }

    @GetMapping("/allTest")
    @Operation(summary = "Fetch all LabTest", description = "Fetch/View all LabTest", tags = { "LabTest" })
    public ResponseEntity<CustomResponse> getAllTest(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size){
        return labTestService.getAllLabTest(page, size);
    }

    @GetMapping("/labTest/{testName}")
    @Operation(summary = "Find Test by Test name", description = "Provide a test name to view a test", tags = { "LabTest" })
    public ResponseEntity<CustomResponse> findTestByTestName(@PathVariable("testName") String testName,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size){
        return labTestService.findTestByTestName(testName, page, size);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Find Test by patientId", description = "Provide a patient's unique Id to view a test", tags = { "LabTest" })
    public ResponseEntity<CustomResponse> findTestByPatientId(@PathVariable("patientId") Long patientId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size){
        return labTestService.findTestByPatientId(patientId, page, size);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Find Test by Status", description = "Provide a test's Status to view related tests", tags = { "LabTest" })
    public ResponseEntity<CustomResponse> findTestByStatus(@PathVariable("status") Status status,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size){
        return labTestService.findTestByStatus(status, page, size);
    }

    @GetMapping("/staff/{staffId}")
    @Operation(summary = "Find Test by StaffId", description = "Provide a staff Unique Id to view tests carried out by that staff", tags = { "LabTest" })
    public ResponseEntity<CustomResponse> findTestByStaffId(@PathVariable("staffId") Long staffId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size){
        return labTestService.findTestByStaffId(staffId, page, size);
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Find test by date", description = "Provide a date as a parameter to view tests carried out on that date. Date parameter should be in the form (2023-08-03)", tags = { "LabTest" })
    public ResponseEntity<CustomResponse> findTestByDate(@PathVariable("date")LocalDate date,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size){
        return labTestService.findTestByDate(date, page, size);
    }

    @PutMapping("/update/{testId}")
    @Operation(summary = "Update a Test", description = "Provide the test Id to update the details about a test", tags = { "LabTest" })
    public ResponseEntity<CustomResponse> updateTest(@PathVariable("testId") Long testId, @RequestBody LabTestRequestDTO requestDTO){
        return labTestService.updateTest(testId, requestDTO);
    }

    @PutMapping("/updateStatus/{testId}")
    @Operation(summary = "Update a test's status", description = "Provide a test unique Id to update the test's status", tags = { "LabTest" })
    public ResponseEntity<CustomResponse> updateTestStatus(@PathVariable("testId") Long testId, @RequestParam String status){
        return labTestService.updateTestStatus(testId, status);
    }

    @DeleteMapping("/delete/{testId}")
    @Operation(summary = "Create a New LabTest", description = "Provide necessary information about a patient to open a file for them", tags = { "LabTest" })
    public ResponseEntity<CustomResponse> deleteTest(@PathVariable("testId") Long testId){
        return labTestService.deleteTest(testId);
    }
}
