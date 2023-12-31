package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.patient.dto.PatientBioRequestDTO;
import com.clepbo.hospital_management_system.patient.dto.PatientResponseDTO;
import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.entity.PatientContactAddress;
import com.clepbo.hospital_management_system.patient.repository.IPatientBioRepository;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import com.clepbo.hospital_management_system.staff.entity.Roles;
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
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientBioService implements IPatientBioService{

    private final IPatientBioRepository patientBioRepository;
    private static final String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$";
    private static final Pattern PATTERN = Pattern.compile(emailRegex);
    @Override
    public ResponseEntity<CustomResponse> createPatientBio(PatientBioRequestDTO patientBioRequestDTO) {
        if(!isEmailValid(patientBioRequestDTO.email())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "provide correct email format"));
        }
        if(!validateRequestFields(patientBioRequestDTO)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "One or more field is empty or equals 'string'"));
        }

        Optional<PatientBio> findPatient = patientBioRepository.findPatientBioByEmail(patientBioRequestDTO.email());
        if(findPatient.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "User/Email already exist"));
        }

        PatientBio patientBio = PatientBio.builder()
                .firstname(patientBioRequestDTO.firstname())
                .lastname(patientBioRequestDTO.lastname())
                .email(patientBioRequestDTO.email())
                .gender(patientBioRequestDTO.gender())
                .dateOfBirth(patientBioRequestDTO.dateOfBirth())
                .phoneNumber(patientBioRequestDTO.phoneNumber())
                .role(Roles.PATIENT)
                .build();
        patientBioRepository.save(patientBio);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), patientBio, "Patient Bio Created Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAllPatientRecord(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstname").ascending());
        Page<PatientBio> getAllPatient = patientBioRepository.findAll(pageable);
        List<PatientResponseDTO> responseDTOS = getAllPatient.stream()
                .map(this::mapToPatientResponse)
                .collect(Collectors.toList());

        CustomResponse customResponse = CustomResponse.builder()
                .status(HttpStatus.OK.name())
                .data(responseDTOS)
                .message("Successful")
                .build();

        return ResponseEntity.ok(customResponse);
    }

    @Override
    public ResponseEntity<CustomResponse> findPatientBioById(Long id) {
        Optional<PatientBio> patientBioOptional = patientBioRepository.findById(id);
        if(!patientBioOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient not found"));
        }
        PatientBio patientBio = patientBioOptional.get();
        PatientResponseDTO patientResponseDTO = PatientResponseDTO.builder()
                .id(patientBio.getId())
                .firstname(patientBio.getFirstname())
                .lastname(patientBio.getLastname())
                .email(patientBio.getEmail())
                .gender(patientBio.getGender())
                .dateOfBirth(patientBio.getDateOfBirth())
                .phoneNumber(patientBio.getPhoneNumber())
                .contactAddress(patientBio.getContactAddress())
                .build();
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), patientResponseDTO, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> updatePatientBio(PatientBioRequestDTO patientBioRequestDTO, Long id) {
        Optional<PatientBio> findPatient = patientBioRepository.findById(id);
        if(!findPatient.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient not found"));

        }

        PatientBio updatedPatientBio = findPatient.get();

        BeanUtils.copyProperties(patientBioRequestDTO, updatedPatientBio, getNullPropertyNames(patientBioRequestDTO));
        patientBioRepository.save(updatedPatientBio);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), updatedPatientBio, "Patient Bio Updated successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> deletePatient(Long id) {
        return null;
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static boolean isEmailValid(String email){
        Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }

    public PatientResponseDTO mapToPatientResponse(PatientBio patientBio){
        return PatientResponseDTO.builder()
                .id(patientBio.getId())
                .firstname(patientBio.getFirstname())
                .lastname(patientBio.getLastname())
                .email(patientBio.getEmail())
                .gender(patientBio.getGender())
                .phoneNumber(patientBio.getPhoneNumber())
                .dateOfBirth(patientBio.getDateOfBirth())
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
