package com.clepbo.hospital_management_system.patient.repository;

import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.entity.PatientMedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPatientMedicalRecordRepository extends JpaRepository<PatientMedicalRecord, Long> {
}
