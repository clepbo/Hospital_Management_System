package com.clepbo.hospital_management_system.appointment.dto;

import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.staff.entity.Staff;

import java.util.Date;

public record AppointmentRequestDTO(Date date, Date time, PatientBio patientBio, Staff staff, String description) {
}
