package com.clepbo.hospital_management_system.patient.repository;

import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPatientBioRepository extends JpaRepository<PatientBio, Long> {
    Optional<PatientBio> findPatientBioByEmail(String email);
}
