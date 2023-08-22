package com.clepbo.hospital_management_system.patient.repository;

import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.patient.entity.LabTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ILabTestRepository extends JpaRepository<LabTest, Long> {
    Page<LabTest> findLabTestByTestNameContaining(String testName, Pageable pageable);
    Page<LabTest> findLabTestByPatientBio_Id(Long patientId, Pageable pageable);
    Page<LabTest> findLabTestByTestStatus(Status status, Pageable pageable);
    Page<LabTest> findLabTestByCarriedOutBy_Id(Long staffId, Pageable pageable);
    Page<LabTest> findLabTestByTestDate(LocalDate testDate, Pageable pageable);
}
