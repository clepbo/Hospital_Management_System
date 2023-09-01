package com.clepbo.hospital_management_system.staff.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_CREATE("admin:create"),
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin;delete"),
    DOCTOR_CREATE("doctor:create"),
    DOCTOR_READ("doctor:read"),
    DOCTOR_UPDATE("doctor:update"),
    DOCTOR_DELETE("doctor;delete"),
    RECEPTIONIST_ADMIN_CREATE("receptionist:create"),
    RECEPTIONIST_ADMIN_READ("receptionist:read"),
    RECEPTIONIST_ADMIN_UPDATE("receptionist:update"),
    RECEPTIONIST_ADMIN_DELETE("receptionist;delete"),
    NURSE_CREATE("nurse:create"),
    NURSE_READ("nurse:read"),
    NURSE_UPDATE("nurse:update"),
    NURSE_DELETE("nurse;delete"),
    LAB_TECHNICIAN_CREATE("lab_technician:create"),
    LAB_TECHNICIAN_READ("lab_technician:read"),
    LAB_TECHNICIAN_UPDATE("lab_technician:update"),
    LAB_TECHNICIAN_DELETE("lab_technician;delete")
    ;


    @Getter
    private final String permission;
}
