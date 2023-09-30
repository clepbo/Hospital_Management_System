package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.patient.dto.PatientProfilePictureResponseDTO;
import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.entity.PatientProfilePicture;
import com.clepbo.hospital_management_system.patient.repository.IPatientBioRepository;
import com.clepbo.hospital_management_system.patient.repository.IPatientProfilePictureRepository;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientProfilePictureService implements IPatientProfilePictureService {

    private final IPatientProfilePictureRepository repository;
    private final IPatientBioRepository patientBioRepository;
    private final static String FILE_PATH = "C:/Users/Israel Oni/IdeaProjects/Hospital Management System/src/main/resources/static/patientProfilePictures/";

    @Override
    public ResponseEntity<CustomResponse> addProfilePicture(Long patientId, MultipartFile file) throws IOException {
        Optional<PatientBio> findPatient = patientBioRepository.findById(patientId);
        Optional<PatientProfilePicture> patientProfilePicture = repository.findPatientProfilePictureByPatientBio_Id(patientId);
        if(findPatient.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient not found"));
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

        PatientBio patientBio = findPatient.get();
        String fileName = patientBio.getLastname().toLowerCase()+patientBio.getId()+"."+fileType;
        String imagePath = FILE_PATH+fileName;

        PatientProfilePicture profilePicture;
        if (patientProfilePicture.isPresent()) {
            // If a profile picture exists, update it
            profilePicture = patientProfilePicture.get();
            profilePicture.setFileName(patientBio.getLastname().toLowerCase() + patientBio.getId());
            profilePicture.setFileType(fileType);
            profilePicture.setFilePath(imagePath);
        } else {
            // If no profile picture exists, create a new one
            profilePicture = PatientProfilePicture.builder()
                    .fileName(patientBio.getLastname().toLowerCase() + patientBio.getId())
                    .fileType(fileType)
                    .filePath(imagePath)
                    .patientBio(patientBio)
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
        Optional<PatientProfilePicture> profilePictureOptional = repository.findById(id);
        if(profilePictureOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Profile picture not found!"));
        }

        PatientProfilePicture profilePicture = profilePictureOptional.get();
        PatientProfilePictureResponseDTO responseDTO = PatientProfilePictureResponseDTO.builder()
                .filepath(profilePicture.getFilePath())
                .build();

        return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), responseDTO, "Profile Picture found!"));
    }

    @Override
    public ResponseEntity<CustomResponse> getProfilePictureByPatientId(Long patientId) {
        Optional<PatientBio> patientBioOptional = patientBioRepository.findById(patientId);
        Optional<PatientProfilePicture> profilePictureOptional = repository.findPatientProfilePictureByPatientBio_Id(patientId);
        if(patientBioOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient not found!"));
        }
        if(profilePictureOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient profile picture not found!"));
        }

        PatientProfilePicture profilePicture = profilePictureOptional.get();
        PatientProfilePictureResponseDTO responseDTO = PatientProfilePictureResponseDTO.builder()
                .filepath(profilePicture.getFilePath())
                .build();

        return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), responseDTO, "Profile Picture found!"));
    }

    @Override
    public ResponseEntity<CustomResponse> deleteProfilePicture(Long id) {
        Optional<PatientProfilePicture> profilePictureOptional = repository.findById(id);
        if(profilePictureOptional.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Profile picture not found!"));
        }

        repository.deleteById(id);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Profile Picture deleted successfully!"));
    }

    public static BufferedImage compressImage(MultipartFile multipartFile) throws IOException {
        // Create an InputStream from the MultipartFile
        try (InputStream inputStream = multipartFile.getInputStream()) {
            BufferedImage originalImage = ImageIO.read(inputStream);

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
