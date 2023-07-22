package com.clepbo.hospital_management_system.appointment.dto;

import com.clepbo.hospital_management_system.config.LocalTimeSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentRequestDTO(

        @ApiModelProperty(value = "Date of the appointment (format: yyyy-MM-dd)", example = "2023-07-31")
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @ApiModelProperty(value = "Time of the appointment (format: HH:mm:ss)", example = "14:30:00")
        @JsonFormat(pattern = "HH:mm:ss")
        @JsonSerialize(using = LocalTimeSerializer.class)
        LocalTime time,
        String patientId,
        String staffId,
        String description) {
}
