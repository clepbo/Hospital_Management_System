package com.clepbo.hospital_management_system.patient.repository;

import com.clepbo.hospital_management_system.patient.entity.PatientProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPatientProfilePictureRepository extends JpaRepository<PatientProfilePicture, Long> {
    Optional<PatientProfilePicture> findPatientProfilePictureByPatientBio_Id(Long patientId);
}
