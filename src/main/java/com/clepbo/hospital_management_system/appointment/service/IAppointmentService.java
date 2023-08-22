package com.clepbo.hospital_management_system.appointment.service;

import com.clepbo.hospital_management_system.appointment.dto.AppointmentRequestDTO;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;

public interface IAppointmentService {
    ResponseEntity<CustomResponse> createAppointment(AppointmentRequestDTO requestDTO);
    ResponseEntity<CustomResponse> createAppointmentByRequest(Long requestId, AppointmentRequestDTO requestDTO);
    ResponseEntity<CustomResponse> getAllAppointment(int page, int size);
    ResponseEntity<CustomResponse> getAppointmentById(Long appointmentId);
    ResponseEntity<CustomResponse> getAppointmentByStaffId(Long staffId, int page, int size);
    ResponseEntity<CustomResponse> getAppointmentByPatientId(Long patientId, int page, int size);
    ResponseEntity<CustomResponse> getAppointmentByDate(LocalDate date, int page, int size);
    ResponseEntity<CustomResponse> rescheduleAppointment(Long appointmentId, LocalDate date, String time);
    ResponseEntity<CustomResponse> updateAppointmentStatus(Long appointmentId, String status);
    ResponseEntity<CustomResponse> updateAppointment(Long appointmentId, AppointmentRequestDTO requestDTO);
    ResponseEntity<CustomResponse> deleteAppointment(Long appointmentId);
    public void expiredAppointment();
    public void notifyPatient();
}
