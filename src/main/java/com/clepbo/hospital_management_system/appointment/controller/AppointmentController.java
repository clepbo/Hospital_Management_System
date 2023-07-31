package com.clepbo.hospital_management_system.appointment.controller;

import com.clepbo.hospital_management_system.appointment.dto.AppointmentRequestDTO;
import com.clepbo.hospital_management_system.appointment.service.IAppointmentService;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
@Tag(name = "Appointment", description = "Hospital Management System Appointment Module")
public class AppointmentController {

    private final IAppointmentService appointmentService;

    @PostMapping("/create")
    @Operation(summary = "Create New Appointment", description = "Provide the staff and patient unique Id to create an appointment. Set time value as (14:00:00)", tags = { "Appointment" })
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<CustomResponse> createAppointment(@RequestBody AppointmentRequestDTO requestDTO){
        return appointmentService.createAppointment(requestDTO);
    }

    @PostMapping("/create/{requestId}")
    @Operation(summary = "Create a new appointment based on a patient's request to see a doctor", description = "Provide the request unique Id to create an appointment for a patient to see a doctor. Set time value as (14:00:00)", tags = { "Appointment" })
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<CustomResponse> createAppointmentByRequest(@PathVariable("requestId") Long requestId, @RequestBody AppointmentRequestDTO requestDTO){
        return appointmentService.createAppointmentByRequest(requestId, requestDTO);
    }

    @GetMapping("/")
    @Operation(summary = "View all Appointment", description = "View all Appointment", tags = { "Appointment" })
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<CustomResponse> getAllAppointment(){
        return appointmentService.getAllAppointment();
    }

    @GetMapping("/{appointmentId}")
    @Operation(summary = "Find an appointment with the appointmentId", description = "Provide the appointment unique Id to find/fetch the appointment", tags = { "Appointment" })
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<CustomResponse> getAppointmentById(@PathVariable("appointmentId") Long appointmentId){
        return appointmentService.getAppointmentById(appointmentId);
    }

    @GetMapping("/staffAppointment/{staffId}")
    @Operation(summary = "Find appointment with staffId", description = "Provide a staff unique Id to find/fetch the staff's appointments", tags = { "Appointment" })
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<CustomResponse> getAppointmentByStaffId(@PathVariable("staffId") Long staffId){
        return appointmentService.getAppointmentByStaffId(staffId);
    }

    @GetMapping("/patientAppointment/{patientId}")
    @Operation(summary = "Find appointment with patientId", description = "Provide a patient's unique Id to find/fetch the patients appointments", tags = { "Appointment" })
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<CustomResponse> getAppointmentByPatientId(@PathVariable("patientId") Long patientId){
        return appointmentService.getAppointmentByPatientId(patientId);
    }

    @GetMapping("/appointmentDate/{date}")
    @Operation(summary = "Find appointment by date", description = "Provide a Unique date to find/fetch appointment in that date. Date format should be (2023-07-31)", tags = { "Appointment" })
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<CustomResponse> getAppointmentByDate(@PathVariable("date") LocalDate date){
        return appointmentService.getAppointmentByDate(date);
    }

    @PutMapping("/reschedule/{appointmentId}")
    @Operation(summary = "Reschedule an Appointment", description = "Provide an appointment unique Id to reschedule the appointment", tags = { "Appointment" })
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<CustomResponse> rescheduleAppointment(@PathVariable("appointmentId") Long appointmentId, @RequestParam LocalDate date, @RequestParam LocalTime time){
        return appointmentService.rescheduleAppointment(appointmentId, date, time);
    }

    @PutMapping("/updateStatus/{appointmentId}")
    @Operation(summary = "Update an appointment", description = "Provide an appointment unique Id to update the appointment status", tags = { "Appointment" })
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<CustomResponse> updateAppointmentStatus(@PathVariable("appointmentId") Long appointmentId, @RequestParam String status){
        return appointmentService.updateAppointmentStatus(appointmentId, status);
    }

    @PutMapping("/update/{appointmentId}")
    @Operation(summary = "Update an appointment", description = "Provide an appointment unique Id to update/edit the appointment", tags = { "Appointment" })
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<CustomResponse> updateAppointment(@PathVariable("appointmentId") Long appointmentId, @RequestBody AppointmentRequestDTO requestDTO){
        return appointmentService.updateAppointment(appointmentId,requestDTO);
    }

    @DeleteMapping("/delete/{appointmentId}")
    @Operation(summary = "Delete an appointment", description = "Provide an appointment unique Id to delete the appointment", tags = { "Appointment" })
    @PreAuthorize("hasAnyAuthority('ROLE_RECEPTIONIST', 'ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<CustomResponse> deleteAppointment(@PathVariable("appointmentId") Long appointmentId){
        return appointmentService.deleteAppointment(appointmentId);
    }
}
