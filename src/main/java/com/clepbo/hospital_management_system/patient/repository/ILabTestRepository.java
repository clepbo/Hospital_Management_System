package com.clepbo.hospital_management_system.patient.repository;

import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.patient.entity.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ILabTestRepository extends JpaRepository<LabTest, Long> {
    List<LabTest> findLabTestByTestNameContaining(String testName);
    List<LabTest> findLabTestByPatientBio_Id(Long patientId);
    List<LabTest> findLabTestByTestStatus(Status status);
    List<LabTest> findLabTestByCarriedOutBy_Id(Long staffId);
    List<LabTest> findLabTestByTestDate(LocalDate testDate);
}
