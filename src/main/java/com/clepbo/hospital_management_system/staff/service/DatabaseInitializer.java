package com.clepbo.hospital_management_system.staff.service;

import com.clepbo.hospital_management_system.staff.entity.Staff;
import com.clepbo.hospital_management_system.staff.repository.IStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final IStaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        Optional<Staff> findAdmin = staffRepository.findByEmail("admin@login.com");
        if(!findAdmin.isPresent()){
            Staff staff = Staff.builder()
                    .email("admin@login.com")
                    .password(passwordEncoder.encode("admin"))
                    .roles("ROLE_ADMIN")
                    .status("ACTIVE")
                    .isEnabled(true)
                    .build();
            staffRepository.save(staff);
        }
    }
}
