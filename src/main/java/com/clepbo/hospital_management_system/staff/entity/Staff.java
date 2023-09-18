package com.clepbo.hospital_management_system.staff.entity;

import jakarta.persistence.*;
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
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @NaturalId(mutable = false)
    private String email;
    private String password;
    private boolean isEnabled=false;
    @Column(nullable = true)
    private String gender;
    @Column(nullable = true)
    private String dateOfBirth;
    @Column(nullable = true)
    private String phoneNumber;
    @Column(nullable = true)
    private String status;
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Roles roles;
    @Column(nullable = true)
    private Double salary;
    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StaffAddress> addresses;
    @OneToOne(mappedBy = "staff", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private StaffProfilePicture profilePicture;
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
