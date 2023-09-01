package com.clepbo.hospital_management_system.staff.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static com.clepbo.hospital_management_system.staff.entity.Permission.*;

@RequiredArgsConstructor
public enum Roles {
    ADMIN(
            Set.of(
                    ADMIN_CREATE,
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,

                    DOCTOR_CREATE,
                    DOCTOR_READ,
                    DOCTOR_UPDATE,
                    DOCTOR_DELETE,

                    RECEPTIONIST_ADMIN_CREATE,
                    RECEPTIONIST_ADMIN_READ,
                    RECEPTIONIST_ADMIN_UPDATE,
                    RECEPTIONIST_ADMIN_DELETE,

                    NURSE_CREATE,
                    NURSE_READ,
                    NURSE_UPDATE,
                    NURSE_DELETE,

                    LAB_TECHNICIAN_CREATE,
                    LAB_TECHNICIAN_READ,
                    LAB_TECHNICIAN_UPDATE,
                    LAB_TECHNICIAN_DELETE
            )
    ),
    PATIENT(Collections.emptySet()),
    DOCTOR(
            Set.of(
                    DOCTOR_CREATE,
                    DOCTOR_READ,
                    DOCTOR_UPDATE,
                    DOCTOR_DELETE
            )
    ),
    RECEPTIONIST(
            Set.of(
                    RECEPTIONIST_ADMIN_CREATE,
                    RECEPTIONIST_ADMIN_READ,
                    RECEPTIONIST_ADMIN_UPDATE,
                    RECEPTIONIST_ADMIN_DELETE
            )
    ),
    NURSE(
            Set.of(
                    NURSE_CREATE,
                    NURSE_READ,
                    NURSE_UPDATE,
                    NURSE_DELETE
            )
    ),

    LAB_TECHNICIAN(
            Set.of(
                LAB_TECHNICIAN_CREATE,
                LAB_TECHNICIAN_READ,
                LAB_TECHNICIAN_UPDATE,
                LAB_TECHNICIAN_DELETE
            )
    );

    @Getter
    private final Set<Permission> permissions;

    public Collection<SimpleGrantedAuthority> grantedAuthorities(){
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
