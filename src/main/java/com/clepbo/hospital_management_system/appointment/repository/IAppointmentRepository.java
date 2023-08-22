package com.clepbo.hospital_management_system.appointment.repository;

import com.clepbo.hospital_management_system.appointment.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;
import java.util.List;

public interface IAppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findAppointmentsByStaff_Id(Long staffId, Pageable pageable);
    Page<Appointment> findAppointmentsByPatientBios_Id(Long patientId, Pageable pageable);
    Page<Appointment> findAppointmentsByDate(LocalDate date, Pageable pageable);

    List<Appointment> findAppointmentsByStaff_Id(Long staffId);
    List<Appointment> findAppointmentsByPatientBios_Id(Long patientId);
}
