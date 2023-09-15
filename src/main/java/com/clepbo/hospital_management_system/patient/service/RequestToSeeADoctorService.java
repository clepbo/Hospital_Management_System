package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.notificationService.dto.EmailNotificationDto;
import com.clepbo.hospital_management_system.notificationService.dto.RequestNotification;
import com.clepbo.hospital_management_system.notificationService.service.IMailService;
import com.clepbo.hospital_management_system.notificationService.service.MailService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
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
    private final IMailService mailService;
    private static final String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Override
    public ResponseEntity<CustomResponse> createARequest(RequestToSeeADoctorRequestDTO requestDTO) throws UnsupportedEncodingException {
        if(!validateRequestFields(requestDTO)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "One or more field is empty or equals 'string'"));
        }

        Optional<PatientBio> findPatientBioByEmail  = patientBioRepository.findPatientBioByEmail(requestDTO.patientEmail());

        if(!findPatientBioByEmail.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient doesn't exist " + requestDTO.patientEmail()));
        }

        PatientBio getPatient = findPatientBioByEmail.get();


        if(!findPatientBioByEmail.get().getEmail().equals(requestDTO.patientEmail())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "Email is invalid"));
        }

        PatientBio patientBio = findPatientBioByEmail.get();

        RequestToSeeADoctor requestToSeeADoctor = RequestToSeeADoctor.builder()
                .patientBio(getPatient)
                .reason(requestDTO.reason())
                .status(Status.PENDING)
                .build();
        requestToSeeADoctorRepository.save(requestToSeeADoctor);

        String subject = "RE: APPOINTMENT REQUEST";

        //send mail to patient
        mailService.patientRequest(new RequestNotification(
                subject,
                requestDTO.patientEmail(),
                patientBio.getFirstname() + " " + patientBio.getLastname()
        ));

        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), requestToSeeADoctor, "Request Created Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAllRequest(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("patientBio_firstname").ascending());
        Page<RequestToSeeADoctor> request = requestToSeeADoctorRepository.findAll(pageable);
        List<RequestToSeeADoctorResponseDTO> getAllRequest = request.stream()
                .map(this::mapToResponseDTO)
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
        RequestToSeeADoctorResponseDTO responseDTO = mapToResponseDTO(requestToSeeADoctor);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), responseDTO, "Request Found"));
    }

    @Override
    public ResponseEntity<CustomResponse> viewRequestByPatientId(String patientId, int page, int size) throws UnsupportedEncodingException {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
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

        Page<RequestToSeeADoctor> findPatientRequest = requestToSeeADoctorRepository.findRequestToSeeADoctorByPatientBio_Id(getPatient.getId(), pageable);
        List<RequestToSeeADoctorResponseDTO> patientRequests = findPatientRequest.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        if(patientRequests.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "List is empty"));
        }
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), patientRequests, "Suucessful"));
    }

    @Override
    public ResponseEntity<CustomResponse> viewRequestByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("status").ascending());
        Status statusValue = Status.valueOf(status.toUpperCase());
        Page<RequestToSeeADoctor> findRequestByStatus = requestToSeeADoctorRepository.findRequestToSeeADoctorByStatus(statusValue, pageable);
        List<RequestToSeeADoctorResponseDTO> getAllRequest = findRequestByStatus.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        if(getAllRequest.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "List is empty"));
        }
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), getAllRequest, "Successful"));
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

    public RequestToSeeADoctorResponseDTO mapToResponseDTO(RequestToSeeADoctor requestToSeeADoctor){
        return RequestToSeeADoctorResponseDTO.builder()
                .id(requestToSeeADoctor.getId())
                .reason(requestToSeeADoctor.getReason())
                .patientEmail(requestToSeeADoctor.getPatientBio().getEmail())
                .patientName(requestToSeeADoctor.getPatientBio().getFirstname() + " " + requestToSeeADoctor.getPatientBio().getLastname())
                .dateRequested(requestToSeeADoctor.getCreatedAt())
                .status(requestToSeeADoctor.getStatus().toString())
                .build();
    }

    public static <T> boolean validateRequestFields(T requestDTO) {
        if (requestDTO == null) {
            return false; // Handle null input gracefully
        }

        // Get all fields in the request DTO class
        Field[] fields = requestDTO.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(requestDTO);

                if ((fieldValue != null && fieldValue.toString().equals("string")) || Objects.equals(fieldValue, "")) {
                    return false; // Found a field with value "String"
                }
            } catch (IllegalAccessException e) {
                // Handle any exceptions if needed
                e.printStackTrace();
            }
        }

        return true; // All fields are valid
    }
}
