package com.clepbo.hospital_management_system.patient.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientProfilePicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String fileType;
    private String filePath;
    @OneToOne
    @JoinColumn(name = "patientBioId")
    @NaturalId
    private PatientBio patientBio;
}
