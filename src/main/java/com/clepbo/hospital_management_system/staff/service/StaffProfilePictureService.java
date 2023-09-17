package com.clepbo.hospital_management_system.staff.service;

import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import com.clepbo.hospital_management_system.staff.dto.StaffBioDataRequestDto;
import com.clepbo.hospital_management_system.staff.entity.Staff;
import com.clepbo.hospital_management_system.staff.entity.StaffProfilePicture;
import com.clepbo.hospital_management_system.staff.repository.IStaffProfilePictureRepository;
import com.clepbo.hospital_management_system.staff.repository.IStaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffProfilePictureService implements IStaffProfilePictureService{

    private final IStaffProfilePictureRepository repository;
    private final IStaffRepository staffRepository;
    private final static String FILE_PATH = "src/main/resources/static/staffProfilePictures/";

    @Override
    public ResponseEntity<CustomResponse> addProfilePicture(Long staffId, MultipartFile file) throws IOException {
        Optional<Staff> findStaff = staffRepository.findById(staffId);
        Optional<StaffProfilePicture> staffProfilePicture = repository.findStaffProfilePictureByStaff_Id(staffId);
        if(findStaff.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Staff not found"));
        }
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "File Cannot be empty"));
        }

        String originalFileName = file.getOriginalFilename();
        assert originalFileName != null;
        String fileType = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();
        List<String> possibleFileType = Arrays.asList("jpg", "jpeg", "png", "gif");
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        if(!possibleFileType.contains(fileType)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "Invalid file type"));
        }
        if(file.getSize() > 1048576){
            bufferedImage = compressImage(file);
        }

        Staff staff = findStaff.get();
        String fileName = staff.getLastName().toLowerCase()+staff.getId()+"."+fileType;
        String imagePath = FILE_PATH+fileName;

        StaffProfilePicture profilePicture;
        if (staffProfilePicture.isPresent()) {
            // If a profile picture exists, update it
            profilePicture = staffProfilePicture.get();
            profilePicture.setFileName(staff.getLastName().toLowerCase() + staff.getId());
            profilePicture.setFileType(fileType);
            profilePicture.setFilePath(imagePath);
        } else {
            // If no profile picture exists, create a new one
            profilePicture = StaffProfilePicture.builder()
                    .fileName(staff.getLastName().toLowerCase() + staff.getId())
                    .fileType(fileType)
                    .filePath(imagePath)
                    .staff(staff)
                    .build();
        }
        try {
            // Save the profile picture data
            repository.save(profilePicture);

            // Write the image to the file system
            ImageIO.write(bufferedImage, fileType, new File(imagePath));

            return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), profilePicture, "File uploaded successfully"));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CustomResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), "Failed to save the image"));
        }
    }

    @Override
    public ResponseEntity<CustomResponse> getProfilePictureById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<CustomResponse> deleteProfilePicture(Long id) {
        return null;
    }

    public static BufferedImage compressImage(MultipartFile multipartFile) throws IOException {
        // Create an InputStream from the MultipartFile
        try (InputStream inputStream = multipartFile.getInputStream()) {
            BufferedImage originalImage = javax.imageio.ImageIO.read(inputStream);

            // Compress the image if it's larger than 1MB
            if (multipartFile.getSize() > 1000000) { // 1MB in bytes
                return Thumbnails.of(originalImage)
                        .size(800, 600) // Set your desired dimensions here
                        .outputQuality(0.5) // Adjust the quality as needed
                        .asBufferedImage();
            } else {
                // If the file is smaller than 1MB, return the original image without compression
                return originalImage;
            }
        }
    }

}
