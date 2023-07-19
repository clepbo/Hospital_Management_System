package com.clepbo.hospital_management_system.patient.entity;

import com.clepbo.hospital_management_system.appointment.entity.Appointment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientMedicalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symptoms;
    private String complaints;
    private String prescribedMedication;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id")
    private List<Appointment> appointment;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medical_record")
    private PatientMedicalRecord medicalRecord;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    private PatientBio patientBio;
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
