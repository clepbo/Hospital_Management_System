package com.clepbo.hospital_management_system.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record AppointmentRequestDTO(

        @ApiModelProperty(value = "Date of the appointment (format: yyyy-MM-dd)", example = "2023-07-31")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        String time,
        String patientId,
        String staffId,
        String description) {
}
