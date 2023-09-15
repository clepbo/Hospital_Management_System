package com.clepbo.hospital_management_system.staff.service;

import com.clepbo.hospital_management_system.security.CustomUserDetails;
import com.clepbo.hospital_management_system.security.JwtService;
import com.clepbo.hospital_management_system.staff.dto.StaffLoginRequestDTO;
import com.clepbo.hospital_management_system.staff.entity.Roles;
import com.clepbo.hospital_management_system.staff.entity.Staff;
import com.clepbo.hospital_management_system.staff.repository.IStaffRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
                    .firstName("Admin")
                    .lastName("Admin")
                    .email("admin@login.com")
                    .password(passwordEncoder.encode("admin"))
                    .roles(Roles.ADMIN)
                    .status("ACTIVE")
                    .isEnabled(true)
                    .build();
            staffRepository.save(staff);
        }
    }
}
