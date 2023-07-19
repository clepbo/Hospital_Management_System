package com.clepbo.hospital_management_system.patient.repository;

import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.entity.PatientMedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPatientMedicalHistoryRepository extends JpaRepository<PatientMedicalHistory, Long> {
}
