package com.clepbo.hospital_management_system.patient.repository;

import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPatientBioRepository extends JpaRepository<PatientBio, Long> {
}
