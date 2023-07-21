package com.clepbo.hospital_management_system.appointment.service;

import com.clepbo.hospital_management_system.appointment.dto.AppointmentRequestDTO;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public interface IAppointmentService {
    ResponseEntity<CustomResponse> createAppointment(AppointmentRequestDTO requestDTO);
    ResponseEntity<CustomResponse> getAllAppointment();
    ResponseEntity<CustomResponse> getAppointmentById(Long appointmentId);
    ResponseEntity<CustomResponse> getAppointmentByStaffId(Long staffId);
    ResponseEntity<CustomResponse> getAppointmentByPatientId(Long patientId);
    ResponseEntity<CustomResponse> getAppointmentByPatientName(String name);
    ResponseEntity<CustomResponse> getAppointmentByDate(Date date);
    ResponseEntity<CustomResponse> updateAppointment(AppointmentRequestDTO requestDTO, Long appointmentId);
    ResponseEntity<CustomResponse> deleteAppointment(Long appointmentId);
}
