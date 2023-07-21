package com.clepbo.hospital_management_system.appointment.repository;

import com.clepbo.hospital_management_system.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAppointmentRepository extends JpaRepository<Appointment, Long> {
}
