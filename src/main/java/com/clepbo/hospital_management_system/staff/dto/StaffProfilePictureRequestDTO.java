package com.clepbo.hospital_management_system.staff.dto;

public record StaffProfilePictureRequestDTO(
       String fileName,
        byte [] fileData,
        String filePath,
        Long staffId
) {
}
