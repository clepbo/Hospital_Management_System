package com.clepbo.hospital_management_system.staff.repository;

import com.clepbo.hospital_management_system.staff.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IStaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByEmail(String email);
}
