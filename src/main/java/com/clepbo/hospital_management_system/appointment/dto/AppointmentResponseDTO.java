package com.clepbo.hospital_management_system.appointment.dto;

import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.staff.entity.Staff;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentResponseDTO {
    private Long id;
    private Date date;
    private Date time;
    private String patientName;
    private String doctorName;
    private Long patientId;
    private Long doctorId;
    private String status;
    private String description;
}
