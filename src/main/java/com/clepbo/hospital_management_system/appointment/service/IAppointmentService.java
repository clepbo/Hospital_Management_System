package com.clepbo.hospital_management_system.appointment.service;

import com.clepbo.hospital_management_system.appointment.dto.AppointmentRequestDTO;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

import java.sql.Time;
import java.util.Date;

public interface IAppointmentService {
    ResponseEntity<CustomResponse> createAppointment(AppointmentRequestDTO requestDTO, Long staffId, Long patientId);
    ResponseEntity<CustomResponse> createAppointmentByRequest(Long requestId, AppointmentRequestDTO requestDTO, Long staffId);
    ResponseEntity<CustomResponse> getAllAppointment();
    ResponseEntity<CustomResponse> getAppointmentById(Long appointmentId);
    ResponseEntity<CustomResponse> getAppointmentByStaffId(Long staffId);
    ResponseEntity<CustomResponse> getAppointmentByPatientId(Long patientId);
    ResponseEntity<CustomResponse> getAppointmentByDate(Date date);
    ResponseEntity<CustomResponse> rescheduleAppointment(Long appointmentId, Date date, Date time);
    ResponseEntity<CustomResponse> updateAppointmentStatus(Long appointmentId, String status);
    ResponseEntity<CustomResponse> updateAppointment(Long appointmentId, AppointmentRequestDTO requestDTO);
    ResponseEntity<CustomResponse> deleteAppointment(Long appointmentId);
}
