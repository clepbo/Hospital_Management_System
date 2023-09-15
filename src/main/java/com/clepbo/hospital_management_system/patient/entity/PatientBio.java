package com.clepbo.hospital_management_system.patient.entity;

import com.clepbo.hospital_management_system.staff.entity.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

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
    @NotBlank(message = "Firstname Required")
    @NotNull
    private String firstname;

    @NotBlank(message = "Lastname Required")
    @NotNull
    private String lastname;

    @NotBlank(message = "Email Required")
    @NotNull
    @NaturalId(mutable = false)
    private String email;

    private String dateOfBirth;
    private String phoneNumber;
    private String gender;

    @Enumerated(EnumType.STRING)
    private Roles role;

    @OneToMany(mappedBy = "patientBio", orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
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
