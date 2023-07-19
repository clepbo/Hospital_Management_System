package com.clepbo.hospital_management_system.patient.entity;

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
public class PatientBio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private String dateOfBirth;
    private String phoneNumber;
    private String gender;
    @OneToMany(mappedBy = "patientBio", orphanRemoval = true)
    private List<PatientContactAddress> contactAddress;
    @OneToMany(mappedBy = "patientBio", orphanRemoval = true)
    private List<PatientMedicalHistory> medicalHistory;
    @OneToMany(mappedBy = "patientBio", orphanRemoval = true)
    private List<PatientMedicalRecord> medicalRecord;
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
