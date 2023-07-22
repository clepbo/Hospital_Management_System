package com.clepbo.hospital_management_system.appointment.repository;

import com.clepbo.hospital_management_system.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IAppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAppointmentsByStaff_Id(Long staffId);
    List<Appointment> findAppointmentsByPatientBios_Id(Long patientId);
    List<Appointment> findAppointmentsByDate(LocalDate date);
}
