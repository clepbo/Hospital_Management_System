package com.clepbo.hospital_management_system.patient.repository;

import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.patient.entity.RequestToSeeADoctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRequestToSeeADoctorRepository extends JpaRepository<RequestToSeeADoctor, Long> {
    List<RequestToSeeADoctor> findRequestToSeeADoctorByPatientBio_Id(Long patientId);
    List<RequestToSeeADoctor> findRequestToSeeADoctorByStatus(Status status);
}
