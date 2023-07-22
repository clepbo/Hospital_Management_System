package com.clepbo.hospital_management_system.appointment.entity;

import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.staff.entity.Staff;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "Appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.DATE)
    @ApiModelProperty(value = "Date of the appointment (format: yyyy-MM-dd)", example = "2023-07-31")
    private Date date;
    @Temporal(TemporalType.TIME)
    @ApiModelProperty(value = "Time of the appointment (format: HH:mm:ss)", example = "14:30:00")
    private Date time;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    private PatientBio patientBios;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    private Staff staff;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String description;
    private Timestamp createdAt;
    private Timestamp modifiedAt;

    @PrePersist
    public void prePersist() {
        createdAt = Timestamp.from(Calendar.getInstance().toInstant());
        modifiedAt = Timestamp.from(Calendar.getInstance().toInstant());
    }

    @PreUpdate
    public void preUpdate() {
        modifiedAt = Timestamp.from(Calendar.getInstance().toInstant());
    }
}
