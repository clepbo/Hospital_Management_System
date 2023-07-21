package com.clepbo.hospital_management_system.appointment.service;

import com.clepbo.hospital_management_system.appointment.dto.AppointmentRequestDTO;
import com.clepbo.hospital_management_system.appointment.entity.Appointment;
import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.appointment.repository.IAppointmentRepository;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService{

    private final IAppointmentRepository appointmentRepository;

    @Override
    public ResponseEntity<CustomResponse> createAppointment(AppointmentRequestDTO requestDTO) {
        if(requestDTO.time() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Time cannot be empty"));
        }
        if(requestDTO.date() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Date cannot be empty"));
        }
        if(requestDTO.staff().getId() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Staff ID cannot be empty"));
        }
        if(requestDTO.patientBio().getId() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Patient Id cannot be empty"));
        }

        Appointment appointment = Appointment.builder()
                .date(requestDTO.date())
                .time(requestDTO.time())
                .description(requestDTO.description())
                //.staff(requestDTO.staff())
               // .patientBios(requestDTO.patientBio())
                .status(Status.PENDING)
                .build();
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> getAllAppointment() {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> getAppointmentById(Long appointmentId) {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> getAppointmentByStaffId(Long staffId) {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> getAppointmentByPatientId(Long patientId) {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> getAppointmentByPatientName(String name) {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> getAppointmentByDate(Date date) {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> updateAppointment(AppointmentRequestDTO requestDTO, Long appointmentId) {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> deleteAppointment(Long appointmentId) {
        return null;
    }
}
