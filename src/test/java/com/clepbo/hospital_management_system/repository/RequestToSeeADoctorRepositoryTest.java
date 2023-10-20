package com.clepbo.hospital_management_system.repository;

import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.patient.dto.PatientBioRequestDTO;
import com.clepbo.hospital_management_system.patient.dto.RequestToSeeADoctorRequestDTO;
import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.entity.RequestToSeeADoctor;
import com.clepbo.hospital_management_system.patient.repository.IPatientBioRepository;
import com.clepbo.hospital_management_system.patient.repository.IRequestToSeeADoctorRepository;
import com.clepbo.hospital_management_system.staff.entity.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RequestToSeeADoctorRepositoryTest {

    @Autowired
    private IRequestToSeeADoctorRepository seeADoctorRepository;

    @Autowired
    private IPatientBioRepository patientBioRepository;

    private RequestToSeeADoctor request;
    private PatientBio patientBio;

    @BeforeEach
    void init(){
        PatientBioRequestDTO patientDetails = mapToPatientDTO();
        patientBio = PatientBio.builder()
                .firstname(patientDetails.firstname())
                .lastname(patientDetails.lastname())
                .email(patientDetails.email())
                .gender(patientDetails.gender())
                .dateOfBirth(patientDetails.dateOfBirth())
                .phoneNumber(patientDetails.phoneNumber())
                .role(Roles.PATIENT)
                .build();

        RequestToSeeADoctorRequestDTO requestDTO = mapToRequest();
        request = RequestToSeeADoctor.builder()
                .patientBio(patientBio)
                .reason(requestDTO.reason())
                .status(Status.PENDING)
                .build();
    }

    @Test
    void createRequest(){
        RequestToSeeADoctor newRequest = seeADoctorRepository.save(request);
        assertNotNull(newRequest);
        assertNotNull(newRequest.getId());
    }

    @Test
    void viewAllRequest(){
        patientBioRepository.save(patientBio);
        seeADoctorRepository.save(request);
        List<RequestToSeeADoctor> getRequests = seeADoctorRepository.findAll();

        assertNotNull(getRequests);
        assertEquals(1, getRequests.size());
    }

    @Test
    void getRequestById(){
        patientBioRepository.save(patientBio);
        RequestToSeeADoctor request1 = seeADoctorRepository.save(request);
        RequestToSeeADoctor getRequest = seeADoctorRepository.findById(request1.getId()).get();
        assertNotNull(getRequest);
        assertEquals(Status.PENDING, request1.getStatus());
        assertEquals("okiki@gmail.com", request1.getPatientBio().getEmail());
    }

    @Test
    void getRequestByPatientId(){
        PatientBio patient = patientBioRepository.save(patientBio);
        RequestToSeeADoctor request1 = seeADoctorRepository.save(request);
        List <RequestToSeeADoctor> getRequests = seeADoctorRepository.findRequestToSeeADoctorByPatientBio_Id(patient.getId());
        assertNotNull(getRequests);
        assertEquals(1, getRequests.size());
        boolean allEmailsMatch = getRequests.stream()
                .allMatch(request -> "okiki@gmail.com".equals(request.getPatientBio().getEmail()));

        assertTrue(allEmailsMatch);
    }

    @Test
    void getRequestByStatus(){
        PatientBio patient = patientBioRepository.save(patientBio);
        RequestToSeeADoctor request1 = seeADoctorRepository.save(request);
        List <RequestToSeeADoctor> getRequests = seeADoctorRepository.findRequestToSeeADoctorByStatus(request1.getStatus());
        assertNotNull(getRequests);
        assertEquals(1, getRequests.size());
        boolean allStatusMatch = getRequests.stream()
                .allMatch(request -> Status.PENDING.equals(request.getStatus()));

        assertTrue(allStatusMatch);
    }

    @Test
    void deleteRequest(){
        PatientBio patient = patientBioRepository.save(patientBio);
        RequestToSeeADoctor request1 = seeADoctorRepository.save(request);
        seeADoctorRepository.deleteById(request1.getId());

        Optional<RequestToSeeADoctor> confirmRequest = seeADoctorRepository.findById(request1.getId());
        assertThat(confirmRequest).isEmpty();
    }

    public RequestToSeeADoctorRequestDTO mapToRequest(){
        return RequestToSeeADoctorRequestDTO.builder()
                .patientEmail("okiki@gmail.com")
                .reason("Unit Testing for Request to see a doctor repository")
                .build();
    }

    public PatientBioRequestDTO mapToPatientDTO(){
        return PatientBioRequestDTO.builder()
                .firstname("Israel")
                .lastname("Oni")
                .email("okiki@gmail.com")
                .gender("Male")
                .dateOfBirth("27/10/2001")
                .phoneNumber("08136793904")
                .build();
    }
}
