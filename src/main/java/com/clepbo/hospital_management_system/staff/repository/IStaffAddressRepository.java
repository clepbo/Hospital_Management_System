package com.clepbo.hospital_management_system.staff.repository;

import com.clepbo.hospital_management_system.staff.entity.StaffAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IStaffAddressRepository extends JpaRepository<StaffAddress, Long> {
    Optional<StaffAddress> findStaffAddressByIdAndStaff_Id(Long addressId, Long staffId);
    List<StaffAddress> findStaffAddressesByStaff_Id(Long staffId);
}
