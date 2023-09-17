package com.clepbo.hospital_management_system.staff.repository;

import com.clepbo.hospital_management_system.staff.entity.StaffProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IStaffProfilePictureRepository extends JpaRepository<StaffProfilePicture, Long> {
    Optional<StaffProfilePicture> findStaffProfilePictureByStaff_Id(Long staffId);
}
