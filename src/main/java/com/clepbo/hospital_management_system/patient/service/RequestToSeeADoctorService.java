package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.patient.dto.RequestToSeeADoctorRequestDTO;
import com.clepbo.hospital_management_system.patient.dto.RequestToSeeADoctorResponseDTO;
import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.entity.RequestToSeeADoctor;
import com.clepbo.hospital_management_system.patient.repository.IPatientBioRepository;
import com.clepbo.hospital_management_system.patient.repository.IRequestToSeeADoctorRepository;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestToSeeADoctorService implements IRequestToSeeADoctorService{

    private final IPatientBioRepository patientBioRepository;
    private final IRequestToSeeADoctorRepository requestToSeeADoctorRepository;
    private static final String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Override
    public ResponseEntity<CustomResponse> createARequest(String patientId, RequestToSeeADoctorRequestDTO requestDTO) throws UnsupportedEncodingException {
        Optional<PatientBio> findPatientById = null;

        if(Pattern.matches(emailRegex, patientId) || StringUtils.isNotBlank(patientId)){
            String decodedEmail = URLDecoder.decode(patientId, "UTF-8");
            findPatientById = patientBioRepository.findPatientBioByEmail(decodedEmail);
        }
        if(StringUtils.isNumeric(patientId)){
            Long longValue = Long.parseLong(patientId);
            findPatientById = patientBioRepository.findById(longValue);
        }

        if(!findPatientById.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient doesn't exist " + patientId));
        }

        PatientBio getPatient = findPatientById.get();
        if(requestDTO.patientEmail() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Email cannot be empty"));
        }
        if(requestDTO.reason() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Reason cannot be empty"));
        }

        if(!findPatientById.get().getEmail().equals(requestDTO.patientEmail())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Email is invalid"));
        }

        RequestToSeeADoctor requestToSeeADoctor = RequestToSeeADoctor.builder()
                .patientBio(getPatient)
                .reason(requestDTO.reason())
                .status(Status.PENDING)
                .build();
        requestToSeeADoctorRepository.save(requestToSeeADoctor);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), requestToSeeADoctor, "Request Created Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAllRequest() {
        List<RequestToSeeADoctor> request = requestToSeeADoctorRepository.findAll();
        List<RequestToSeeADoctorResponseDTO> getAllRequest = request.stream()
                .map(requestToSeeADoctor -> RequestToSeeADoctorResponseDTO.builder()
                        .dateRequested(requestToSeeADoctor.getCreatedAt())
                        .status(String.valueOf(requestToSeeADoctor.getStatus()))
                        .reason(requestToSeeADoctor.getReason())
                        .id(requestToSeeADoctor.getId())
                        .patientEmail(requestToSeeADoctor.getPatientBio().getEmail())
                        .patientName(requestToSeeADoctor.getPatientBio().getFirstname() +" "+requestToSeeADoctor.getPatientBio().getLastname())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), getAllRequest, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> viewRequestById(Long requestId) {
        Optional<RequestToSeeADoctor> request = requestToSeeADoctorRepository.findById(requestId);
        if(!request.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Request not found"));
        }

        RequestToSeeADoctor requestToSeeADoctor = request.get();
        RequestToSeeADoctorResponseDTO responseDTO = RequestToSeeADoctorResponseDTO.builder()
                .id(requestToSeeADoctor.getId())
                .patientEmail(requestToSeeADoctor.getPatientBio().getEmail())
                .patientName(requestToSeeADoctor.getPatientBio().getFirstname() +" "+ requestToSeeADoctor.getPatientBio().getLastname())
                .reason(requestToSeeADoctor.getReason())
                .status(String.valueOf(requestToSeeADoctor.getStatus()))
                .dateRequested(requestToSeeADoctor.getCreatedAt())
                .build();
        return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), responseDTO, "Request Found"));
    }

    @Override
    public ResponseEntity<CustomResponse> viewRequestByPatientId(String patientId) throws UnsupportedEncodingException {
        Optional<PatientBio> findPatientById = null;

        if(Pattern.matches(emailRegex, patientId) || StringUtils.isNotBlank(patientId)){
            String decodedEmail = URLDecoder.decode(patientId, "UTF-8");
            findPatientById = patientBioRepository.findPatientBioByEmail(decodedEmail);
        }
        if(StringUtils.isNumeric(patientId)){
            Long longValue = Long.parseLong(patientId);
            findPatientById = patientBioRepository.findById(longValue);
        }

        if(!findPatientById.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient doesn't exist " + patientId));
        }

        PatientBio getPatient = findPatientById.get();

        List<RequestToSeeADoctor> findPatientRequest = requestToSeeADoctorRepository.findRequestToSeeADoctorByPatientBio_Id(getPatient.getId());
        List<RequestToSeeADoctorResponseDTO> patientRequests = findPatientRequest.stream()
                .map(requestToSeeADoctor -> RequestToSeeADoctorResponseDTO.builder()
                        .dateRequested(requestToSeeADoctor.getCreatedAt())
                        .reason(requestToSeeADoctor.getReason())
                        .status(String.valueOf(requestToSeeADoctor.getStatus()))
                        .build())
                .collect(Collectors.toList());
        if(patientRequests.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "List is empty"));
        }
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), patientRequests, "Suucessful"));
    }

    @Override
    public ResponseEntity<CustomResponse> viewRequestByStatus(String status) {
        Status statusValue = Status.valueOf(status.toUpperCase());
        List<RequestToSeeADoctor> findRequestByStatus = requestToSeeADoctorRepository.findRequestToSeeADoctorByStatus(statusValue);
        List<RequestToSeeADoctorResponseDTO> getAllRequest = findRequestByStatus.stream()
                .map(requestToSeeADoctor -> RequestToSeeADoctorResponseDTO.builder()
                        .dateRequested(requestToSeeADoctor.getCreatedAt())
                        .status(String.valueOf(requestToSeeADoctor.getStatus()))
                        .reason(requestToSeeADoctor.getReason())
                        .id(requestToSeeADoctor.getId())
                        .patientEmail(requestToSeeADoctor.getPatientBio().getEmail())
                        .patientName(requestToSeeADoctor.getPatientBio().getFirstname() +" "+requestToSeeADoctor.getPatientBio().getLastname())
                        .build())
                .collect(Collectors.toList());
        if(getAllRequest.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "List is empty"));
        }
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), getAllRequest, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> updateRequestStatus(Long requestId, RequestToSeeADoctorRequestDTO requestDTO, String status) {
        Optional<RequestToSeeADoctor> request = requestToSeeADoctorRepository.findById(requestId);
        if(!request.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Request not found"));
        }

        RequestToSeeADoctor requestToSeeADoctor = request.get();
        for(Status statuses : Status.values()){
            if(statuses.name().equals(status.toUpperCase())){
                requestToSeeADoctor.setStatus(Status.valueOf(status.toUpperCase()));
                requestToSeeADoctorRepository.save(requestToSeeADoctor);
                return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Request status updated successfully"));
            }
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Invalid status"));
    }

    @Override
    public ResponseEntity<CustomResponse> deleteRequest(Long id) {
        Optional<RequestToSeeADoctor> findRequest = requestToSeeADoctorRepository.findById(id);
        if(!findRequest.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Request not found"));
        }

        requestToSeeADoctorRepository.deleteById(id);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Request Deleted Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> deleteAllPatientRequest(Long patientId) {
        Optional<PatientBio> findPatient = patientBioRepository.findById(patientId);
        if(!findPatient.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient not found"));
        }

        PatientBio patient = findPatient.get();
        List<RequestToSeeADoctor> patientRequests = requestToSeeADoctorRepository.findRequestToSeeADoctorByPatientBio_Id(patient.getId());
        for(RequestToSeeADoctor request : patientRequests){
            requestToSeeADoctorRepository.deleteById(request.getId());
        }
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Patient Requests Deleted Successfully"));
    }
}
