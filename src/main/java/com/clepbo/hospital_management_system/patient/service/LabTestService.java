package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.patient.dto.LabTestRequestDTO;
import com.clepbo.hospital_management_system.patient.dto.LabTestResponseDTO;
import com.clepbo.hospital_management_system.patient.entity.LabTest;
import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.repository.ILabTestRepository;
import com.clepbo.hospital_management_system.patient.repository.IPatientBioRepository;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import com.clepbo.hospital_management_system.staff.entity.Staff;
import com.clepbo.hospital_management_system.staff.repository.IStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabTestService implements ILabTestService{

    private final ILabTestRepository labTestRepository;
    private final IPatientBioRepository patientBioRepository;
    private final IStaffRepository staffRepository;


    @Override
    public ResponseEntity<CustomResponse> createNewLabTest(LabTestRequestDTO requestDTO) {
        Optional<PatientBio> findPatient = patientBioRepository.findById(requestDTO.patientId());
        Optional<Staff> findStaff = staffRepository.findById(requestDTO.staffId());
        if(requestDTO.testName() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Test name cannot be empty"));
        }
        if(requestDTO.patientId() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "PatientId cannot be empty"));
        }
        if(requestDTO.staffId() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "StaffId cannot be empty"));
        }
        if(requestDTO.testDate() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Test Date cannot be empty"));
        }
        if(!findPatient.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Invalid PatientId"));
        }
        if(!findStaff.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Invalid StaffId"));
        }

        PatientBio patientBio = findPatient.get();
        Staff staff = findStaff.get();


        LabTest newLabTest = LabTest.builder()
                .testName(requestDTO.testName())
                .testDate(requestDTO.testDate())
                .testResult(requestDTO.testResult())
                .carriedOutBy(staff)
                .patientBio(patientBio)
                .description(requestDTO.description())
                .recommendations(requestDTO.recommendations())
                .testStatus(Status.valueOf(requestDTO.testStatus().toUpperCase()))
                .build();
        labTestRepository.save(newLabTest);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), "Lab Test Created Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAllLabTest(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("testName").ascending());
        Page<LabTest> getAllTest = labTestRepository.findAll(pageable);
        if(getAllTest.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "List is empty, No Test available"));
        }

        List<LabTestResponseDTO> responseDTOS = getAllTest.stream()
                .map(this::mapToLabTestResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDTOS, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> findTestById(Long id) {
        Optional<LabTest> findTest = labTestRepository.findById(id);
        if(findTest.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Test not found"));
        }

        LabTest test = findTest.get();
        LabTestResponseDTO responseDTO = LabTestResponseDTO.builder()
                .id(test.getId())
                .testName(test.getTestName())
                .testDate(test.getTestDate())
                .testResult(test.getTestResult())
                .testStatus(String.valueOf(test.getTestStatus()))
                .recommendations(test.getRecommendations())
                .description(test.getDescription())
                .patientName(test.getPatientBio().getFirstname() +" "+ test.getPatientBio().getLastname())
                .carriedOutBy(test.getCarriedOutBy().getFirstName() +" "+ test.getCarriedOutBy().getLastName())
                .build();
        return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), responseDTO, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> findTestByTestName(String testName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("testName").ascending());
        Page<LabTest> findTest = labTestRepository.findLabTestByTestNameContaining(testName, pageable);
        if(findTest.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "No test found"));
        }

        List<LabTestResponseDTO> responseDTOS = findTest.stream()
                .map(this::mapToLabTestResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDTOS, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> findTestByPatientId(Long patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("testName").ascending());
        Page<LabTest> findTest = labTestRepository.findLabTestByPatientBio_Id(patientId, pageable);
        if(findTest.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "No test found"));
        }

        List<LabTestResponseDTO> responseDTOS = findTest.stream()
                .map(this::mapToLabTestResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDTOS, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> findTestByStatus(Status status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("testName").ascending());
        Page<LabTest> findTest = labTestRepository.findLabTestByTestStatus(status, pageable);
        if(findTest.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "No test found"));
        }

        List<LabTestResponseDTO> responseDTOS = findTest.stream()
                .map(this::mapToLabTestResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDTOS, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> findTestByStaffId(Long staffId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("testName").ascending());
        Page<LabTest> findTest = labTestRepository.findLabTestByCarriedOutBy_Id(staffId, pageable);
        if(findTest.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "No test found"));
        }

        List<LabTestResponseDTO> responseDTOS = findTest.stream()
                .map(this::mapToLabTestResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDTOS, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> findTestByDate(LocalDate testDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("testDate").descending());
        Page<LabTest> findTest = labTestRepository.findLabTestByTestDate(testDate, pageable);
        if(findTest.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "No test found"));
        }

        List<LabTestResponseDTO> responseDTOS = findTest.stream()
                .map(this::mapToLabTestResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDTOS, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> updateTest(Long id, LabTestRequestDTO requestDTO) {
        Optional<LabTest> findTest = labTestRepository.findById(id);
        if(!findTest.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Test not found"));
        }
        LabTest updatedTest = findTest.get();
        BeanUtils.copyProperties(requestDTO, updatedTest, getNullPropertyNames(requestDTO));
        labTestRepository.save(updatedTest);

        return ResponseEntity.ok(new CustomResponse(HttpStatus.ACCEPTED.name(), "LabTest Updated Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> updateTestStatus(Long id, String status) {
        Optional<LabTest> findTest = labTestRepository.findById(id);
        if(!findTest.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Test not found"));
        }

        LabTest updateTestStatus = findTest.get();
        updateTestStatus.setTestStatus(Status.valueOf(status.toUpperCase()));
        labTestRepository.save(updateTestStatus);

        return ResponseEntity.ok(new CustomResponse(HttpStatus.ACCEPTED.name(), "LabTest Updated Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> deleteTest(Long id) {
        Optional<LabTest> findTest = labTestRepository.findById(id);
        if(!findTest.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Test not found"));
        }

        labTestRepository.deleteById(id);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "LabTest Deleted Successfully"));
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null || srcValue == "string") {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public LabTestResponseDTO mapToLabTestResponse(LabTest labTest){
        return LabTestResponseDTO.builder()
                .testName(labTest.getTestName())
                .testDate(labTest.getTestDate())
                .testResult(labTest.getTestResult())
                .testStatus(String.valueOf(labTest.getTestStatus()))
                .carriedOutBy(labTest.getCarriedOutBy().getFirstName() +" "+ labTest.getCarriedOutBy().getLastName())
                .patientName(labTest.getPatientBio().getFirstname() +" "+ labTest.getPatientBio().getLastname())
                .recommendations(labTest.getRecommendations())
                .description(labTest.getDescription())
                .build();
    }
}
