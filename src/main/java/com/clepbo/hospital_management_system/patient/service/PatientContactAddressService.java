package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.patient.dto.PatientAddressDTO;
import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.entity.PatientContactAddress;
import com.clepbo.hospital_management_system.patient.repository.IPatientAddressRepository;
import com.clepbo.hospital_management_system.patient.repository.IPatientBioRepository;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientContactAddressService implements IPatientContactAddress{

    private final IPatientAddressRepository addressRepository;
    private final IPatientBioRepository patientBioRepository;
    @Override
    public ResponseEntity<CustomResponse> addPatientAddress(Long patientId, PatientAddressDTO addressDTO) {
        Optional<PatientBio> findPatient = patientBioRepository.findById(patientId);
        if(!findPatient.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient doesn't exist"));
        }
        PatientContactAddress contactAddress = PatientContactAddress.builder()
                .address(addressDTO.getAddress())
                .nextOfKinAddress(addressDTO.getNextOfKinAddress())
                .nextOfKin(addressDTO.getNextOfKinAddress())
                .nextOfKinPhoneNumber(addressDTO.getNextOfKinPhoneNumber())
                .nextOfKinRelationship(addressDTO.getNextOfKinRelationship())
                .build();
        addressRepository.save(contactAddress);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), "Patient Address Successfully added"));
    }

    @Override
    public ResponseEntity<CustomResponse> findAddressByPatientId(Long patientId) {
        Optional<PatientBio> findPatient = patientBioRepository.findById(patientId);
        if(findPatient.isPresent()){
            List<PatientContactAddress> contactAddresses = addressRepository.findPatientContactAddressesByPatientBio_Id(patientId);
            List<PatientAddressDTO> patientAddressDTOS = contactAddresses.stream()
                    .map(contactAddress -> PatientAddressDTO.builder()
                            .address(contactAddress.getAddress())
                            .nextOfKinAddress(contactAddress.getNextOfKinAddress())
                            .nextOfKinRelationship(contactAddress.getNextOfKinRelationship())
                            .nextOfKinPhoneNumber(contactAddress.getNextOfKinPhoneNumber())
                            .nextOfKin(contactAddress.getNextOfKinAddress())
                            .build())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), patientAddressDTOS, "Patient Addresses found"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Address not found "));
    }

    @Override
    public ResponseEntity<CustomResponse> findAddressByAddressId(Long addressId) {
        Optional<PatientContactAddress> contactAddress = addressRepository.findById(addressId);
        if(contactAddress.isPresent()){
            PatientContactAddress getAddress = contactAddress.get();
            PatientAddressDTO addressDTO = PatientAddressDTO.builder()
                    .address(getAddress.getAddress())
                    .nextOfKinAddress(getAddress.getNextOfKinAddress())
                    .nextOfKinRelationship(getAddress.getNextOfKinRelationship())
                    .nextOfKin(getAddress.getNextOfKin())
                    .nextOfKinPhoneNumber(getAddress.getNextOfKinPhoneNumber())
                    .build();
            return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), addressDTO, "Patient Address Found"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Address not found " + addressId));
    }

    @Override
    public ResponseEntity<CustomResponse> updatePatientAddress(PatientAddressDTO patientAddressDTO, Long patientId, Long addressId) {
        Optional<PatientBio> findPatient = patientBioRepository.findById(patientId);
        if(findPatient.isPresent()){
            Optional<PatientContactAddress> contactAddress = addressRepository.findById(addressId);
            if(contactAddress.isPresent()){
                PatientContactAddress updatedAddress = contactAddress.get();

                addressRepository.save(updatedAddress);
                return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Successfully Updated Patient Address"));
            }
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Address not found "));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient not found "));
    }

    @Override
    public ResponseEntity<CustomResponse> deletePatientAddressByAddressId(Long addressId) {
        Optional<PatientContactAddress> contactAddress = addressRepository.findById(addressId);
        if(contactAddress.isPresent()){
            addressRepository.deleteById(addressId);
            return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Patient Address Successfully deleted"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Address not found "));
    }

    @Override
    public ResponseEntity<CustomResponse> deleteAllPatientAddressByPatientId(Long patientId) {
        Optional<PatientBio> findPatient = patientBioRepository.findById(patientId);
        if(findPatient.isPresent()){
            List<PatientContactAddress> patientContactAddresses = addressRepository.findPatientContactAddressesByPatientBio_Id(patientId);
            for(PatientContactAddress contactAddress : patientContactAddresses){
                addressRepository.deleteById(contactAddress.getId());
            }
            return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Patient Contact Address Successfully deleted"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient not found "));
    }
}
