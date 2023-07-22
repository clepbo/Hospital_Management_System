package com.clepbo.hospital_management_system.appointment.dto;

import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.staff.entity.Staff;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public record AppointmentRequestDTO(
        @ApiModelProperty(value = "Date of the appointment (format: yyyy-MM-dd)", example = "2023-07-31")Date date,
        @ApiModelProperty(value = "Time of the appointment (format: HH:mm:ss)", example = "14:30:00")Date time,
        String patientId,
        String staffId,
        String description) {
}
