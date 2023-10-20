package com.clepbo.hospital_management_system.repository;

import com.clepbo.hospital_management_system.patient.dto.PatientAddressDTO;
import com.clepbo.hospital_management_system.patient.dto.PatientBioRequestDTO;
import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.entity.PatientContactAddress;
import com.clepbo.hospital_management_system.patient.repository.IPatientAddressRepository;
import com.clepbo.hospital_management_system.patient.repository.IPatientBioRepository;
import com.clepbo.hospital_management_system.staff.entity.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PatientRepositoryTest {

    @Autowired
    private IPatientBioRepository patientBioRepository;

    @Autowired
    private IPatientAddressRepository addressRepository;

    private PatientBio patient1;
    private PatientBio patient2;
    private PatientContactAddress contactAddress;

    @BeforeEach
    void init(){
        PatientBioRequestDTO patientDetails = mapToPatientDTO();
        patient1 = PatientBio.builder()
                .firstname(patientDetails.firstname())
                .lastname(patientDetails.lastname())
                .email(patientDetails.email())
                .gender(patientDetails.gender())
                .dateOfBirth(patientDetails.dateOfBirth())
                .phoneNumber(patientDetails.phoneNumber())
                .role(Roles.PATIENT)
                .build();

        PatientBioRequestDTO patientDetails2 = mapToPatientDTO2();
        patient2 = PatientBio.builder()
                .firstname(patientDetails2.firstname())
                .lastname(patientDetails2.lastname())
                .email(patientDetails2.email())
                .gender(patientDetails2.gender())
                .dateOfBirth(patientDetails2.dateOfBirth())
                .phoneNumber(patientDetails2.phoneNumber())
                .role(Roles.PATIENT)
                .build();

        PatientAddressDTO addressDTO = mapToPatientAddress();
        contactAddress = PatientContactAddress.builder()
                .address(addressDTO.getAddress())
                .nextOfKin(addressDTO.getNextOfKin())
                .nextOfKinAddress(addressDTO.getNextOfKinAddress())
                .nextOfKinPhoneNumber(addressDTO.getNextOfKinPhoneNumber())
                .nextOfKinRelationship(addressDTO.getNextOfKinRelationship())
                .patientBio(patient1)
                .build();
    }

    @Test
    void createPatient(){
        PatientBio newPatient = patientBioRepository.save(patient1);
        assertNotNull(newPatient);
        assertNotNull(newPatient.getId());
    }

    @Test
    void getAllPatient(){
        patientBioRepository.save(patient1);
        patientBioRepository.save(patient2);

        List<PatientBio> getPatients = patientBioRepository.findAll();
        assertNotNull(getPatients);
        assertEquals(2, getPatients.size());
    }

    @Test
    void getPatientById(){
        PatientBio patientBio = patientBioRepository.save(patient2);
        Optional<PatientBio> getPatient = patientBioRepository.findById(patientBio.getId());
        PatientBio savedPatient = getPatient.get();

        assertNotNull(savedPatient);
        assertThat(savedPatient.getId()).isEqualTo(patientBio.getId());
        assertEquals("Abraham", savedPatient.getFirstname());
    }

    @Test
    void updatePatient(){
        PatientBio patientBio = patientBioRepository.save(patient2);
        PatientBio updatedPatient = patientBioRepository.findById(patientBio.getId()).get();
        updatedPatient.setFirstname("Asuquo");
        patientBioRepository.save(updatedPatient);

        assertNotNull(updatedPatient);
        assertEquals("Asuquo", updatedPatient.getFirstname());
        assertEquals(updatedPatient.getId(), patientBio.getId());
    }

    @Test
    void deletePatient(){
        PatientBio patientBio = patientBioRepository.save(patient1);
        patientBioRepository.deleteById(patientBio.getId());

        List<PatientBio> getPatients = patientBioRepository.findAll();
        Optional<PatientBio> deletedPatient = patientBioRepository.findById(patientBio.getId());

        assertThat(deletedPatient).isEmpty();
        assertEquals(0, getPatients.size());
    }

    @Test
    void addPatientAddress(){
        PatientBio patientBio = patientBioRepository.save(patient1);
        PatientContactAddress newAddress = addressRepository.save(contactAddress);

        assertNotNull(newAddress);
        assertNotNull(newAddress.getId());
        assertEquals(patientBio.getId(), newAddress.getPatientBio().getId());
    }

    @Test
    void getAddress(){
        patientBioRepository.save(patient1);
        addressRepository.save(contactAddress);

        List<PatientContactAddress> getAddresses = addressRepository.findAll();
        assertNotNull(getAddresses);
        assertEquals(1, getAddresses.size());
    }

    @Test
    void getAddressById(){
        patientBioRepository.save(patient1);
        PatientContactAddress address = addressRepository.save(contactAddress);

        PatientContactAddress findAddress = addressRepository.findById(address.getId()).get();
        assertNotNull(findAddress);
        assertEquals(address.getId(), findAddress.getId());
        assertEquals("Phase 3, Zone 4, Back of Police Station", findAddress.getAddress());
    }

    @Test
    void getAddressByPatientId(){
        PatientBio patientBio = patientBioRepository.save(patient1);
        PatientContactAddress address = addressRepository.save(contactAddress);

        List<PatientContactAddress> getAddress = addressRepository.findPatientContactAddressesByPatientBio_Id(patientBio.getId());
        assertNotNull(getAddress);
        assertEquals(1, getAddress.size());
    }

    @Test
    void updateAddress(){
        patientBioRepository.save(patient1);
        PatientContactAddress address = addressRepository.save(contactAddress);
        PatientContactAddress updatedAddress = addressRepository.findById(address.getId()).get();

        updatedAddress.setNextOfKinPhoneNumber("08104795169");
        updatedAddress = addressRepository.findById(address.getId()).get();

        assertNotNull(updatedAddress);
        assertEquals("08104795169", updatedAddress.getNextOfKinPhoneNumber());
    }

    @Test
    void deleteAddress(){
        patientBioRepository.save(patient1);
        PatientContactAddress address = addressRepository.save(contactAddress);
        addressRepository.deleteById(address.getId());

        Optional<PatientContactAddress> confirmDelete = addressRepository.findById(address.getId());
        assertThat(confirmDelete).isEmpty();
    }

    public PatientBioRequestDTO mapToPatientDTO(){
        return PatientBioRequestDTO.builder()
                .firstname("Israel")
                .lastname("Oni")
                .email("okiki@gmail.com")
                .gender("Male")
                .dateOfBirth("27/10/2001")
                .phoneNumber("08136793904")
                .build();
    }

    public PatientBioRequestDTO mapToPatientDTO2(){
        return PatientBioRequestDTO.builder()
                .firstname("Abraham")
                .lastname("Peter")
                .email("peters@gmail.com")
                .gender("Male")
                .dateOfBirth("23/05/1996")
                .phoneNumber("08036770752")
                .build();
    }

    public PatientAddressDTO mapToPatientAddress(){
        return PatientAddressDTO.builder()
                .address("Phase 3, Zone 4, Back of Police Station")
                .nextOfKin("Oni Emmanuel")
                .nextOfKinAddress("No. 3, Bowen Quarters, Iwo")
                .nextOfKinPhoneNumber("08136793904")
                .nextOfKinRelationship("Brother")
                .build();
    }
}
