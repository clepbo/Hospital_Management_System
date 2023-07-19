package com.clepbo.hospital_management_system.patient.repository;

import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.entity.PatientContactAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPatientAddressRepository extends JpaRepository<PatientContactAddress, Long> {
    List<PatientContactAddress> findPatientContactAddressesByPatientBio_Id(Long patientId);
    List<PatientContactAddress> deletePatientContactAddressesByPatientBio_Id(Long patientId);
}
