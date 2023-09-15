package com.clepbo.hospital_management_system.patient.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Calendar;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientContactAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String address;
    private String nextOfKin;
    private String nextOfKinPhoneNumber;
    private String nextOfKinAddress;
    private String nextOfKinRelationship;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @JsonBackReference
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
