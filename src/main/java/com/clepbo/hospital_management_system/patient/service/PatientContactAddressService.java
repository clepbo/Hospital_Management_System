package com.clepbo.hospital_management_system.patient.service;

import com.clepbo.hospital_management_system.patient.dto.PatientAddressDTO;
import com.clepbo.hospital_management_system.patient.dto.PatientAddressResponseDTO;
import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.entity.PatientContactAddress;
import com.clepbo.hospital_management_system.patient.repository.IPatientAddressRepository;
import com.clepbo.hospital_management_system.patient.repository.IPatientBioRepository;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

        PatientBio patientBio = findPatient.get();

        PatientContactAddress contactAddress = PatientContactAddress.builder()
                .address(addressDTO.getAddress())
                .nextOfKinAddress(addressDTO.getNextOfKinAddress())
                .nextOfKin(addressDTO.getNextOfKin())
                .nextOfKinPhoneNumber(addressDTO.getNextOfKinPhoneNumber())
                .nextOfKinRelationship(addressDTO.getNextOfKinRelationship())
                .patientBio(patientBio)
                .build();
        addressRepository.save(contactAddress);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), contactAddress, "Patient Address Successfully added"));
    }

    @Override
    public ResponseEntity<CustomResponse> findAddressByPatientId(Long patientId) {
        Optional<PatientBio> findPatient = patientBioRepository.findById(patientId);
        if(findPatient.isPresent()){
            List<PatientContactAddress> contactAddresses = addressRepository.findPatientContactAddressesByPatientBio_Id(patientId);

            List<PatientAddressResponseDTO> responseDTOS = contactAddresses.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), responseDTOS, "Patient Addresses found"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Address not found "));
    }

    @Override
    public ResponseEntity<CustomResponse> findAddressByAddressId(Long addressId) {
        Optional<PatientContactAddress> contactAddress = addressRepository.findById(addressId);
        if(contactAddress.isPresent()){
            PatientContactAddress getAddress = contactAddress.get();
            PatientAddressResponseDTO addressDTO = mapToResponseDTO(getAddress);
            return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), addressDTO, "Patient Address Found"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Address not found " + addressId));
    }

    @Override
    public ResponseEntity<CustomResponse> updatePatientAddress(PatientAddressDTO patientAddressDTO, Long patientId, Long addressId) {
        Optional<PatientBio> findPatient = patientBioRepository.findById(patientId);
        if(!findPatient.isPresent()) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient not found "));
        }
        Optional<PatientContactAddress> contactAddress = addressRepository.findById(addressId);
        if(!contactAddress.isPresent()) {
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Address not found "));
        }

        PatientContactAddress updatedAddress = contactAddress.get();
        BeanUtils.copyProperties(patientAddressDTO, updatedAddress, getNullPropertyNames(patientAddressDTO));

        addressRepository.save(updatedAddress);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), updatedAddress, "Successfully Updated Patient Address"));


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

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public PatientAddressResponseDTO mapToResponseDTO(PatientContactAddress contactAddress){
        return PatientAddressResponseDTO.builder()
                .id(contactAddress.getId())
                .address(contactAddress.getAddress())
                .nextOfKin(contactAddress.getNextOfKin())
                .nextOfKinRelationship(contactAddress.getNextOfKinRelationship())
                .nextOfKinAddress(contactAddress.getNextOfKinAddress())
                .nextOfKinPhoneNumber(contactAddress.getNextOfKinPhoneNumber())
                .build();
    }

}
