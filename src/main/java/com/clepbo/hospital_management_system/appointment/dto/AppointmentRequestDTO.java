package com.clepbo.hospital_management_system.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentRequestDTO(

        @ApiModelProperty(value = "Date of the appointment (format: yyyy-MM-dd)", example = "2023-07-31")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

//        @ApiModelProperty(value = "Time of the appointment (format: HH:mm:ss)", example = "14:30:00")
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        String time,
        String patientId,
        String staffId,
        String description) {
}
