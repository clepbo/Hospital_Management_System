package com.clepbo.hospital_management_system.repository;

import com.clepbo.hospital_management_system.staff.dto.StaffAddressDTO;
import com.clepbo.hospital_management_system.staff.dto.StaffRequestDto;
import com.clepbo.hospital_management_system.staff.entity.Roles;
import com.clepbo.hospital_management_system.staff.entity.Staff;
import com.clepbo.hospital_management_system.staff.entity.StaffAddress;
import com.clepbo.hospital_management_system.staff.repository.IStaffAddressRepository;
import com.clepbo.hospital_management_system.staff.repository.IStaffRepository;

import org.junit.jupiter.api.AfterEach;
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
public class StaffRepositoryTest {

    @Autowired
    private IStaffRepository staffRepository;

    @Autowired
    private IStaffAddressRepository addressRepository;
    private Staff staff1;
    private Staff staff2;
    private StaffAddress staffAddress;
    private StaffAddress staffAddress2;

    @BeforeEach
    void init(){
        StaffRequestDto requestDto = mapToStaff1();
        staff1 = Staff.builder()
                .firstName(requestDto.firstName())
                .lastName(requestDto.lastName())
                .email(requestDto.email())
                .password(requestDto.password())
                .roles(requestDto.roles())
                .build();

        StaffRequestDto requestDto2 = mapToStaff2();
        staff2 = Staff.builder()
                .firstName(requestDto2.firstName())
                .lastName(requestDto2.lastName())
                .email(requestDto2.email())
                .password(requestDto2.password())
                .roles(requestDto2.roles())
                .build();

        StaffAddressDTO addressDTO = mapToStaffAddress();
        staffAddress = StaffAddress.builder()
                .staff(staff1)
                .country(addressDTO.getCountry())
                .state(addressDTO.getState())
                .city(addressDTO.getCity())
                .street(addressDTO.getStreet())
                .postalCode(addressDTO.getPostalCode())
                .build();

        StaffAddressDTO addressDTO2 = mapToStaffAddress2();
        staffAddress2 = StaffAddress.builder()
                .staff(staff1)
                .country(addressDTO2.getCountry())
                .state(addressDTO2.getState())
                .city(addressDTO2.getCity())
                .street(addressDTO2.getStreet())
                .postalCode(addressDTO2.getPostalCode())
                .build();
    }


    @AfterEach
    public void afterSetup() {
        addressRepository.deleteAll();
        staffRepository.deleteAll();
    }

    @Test
    void createStaff(){
        Staff newStaff = staffRepository.save(staff1);
        assertNotNull(newStaff);
        assertThat(newStaff.getId()).isNotEqualTo(null);
        assertThat(newStaff.getFirstName()).isEqualTo("Israel");
    }

    @Test
    void getAllStaff(){
        staffRepository.save(staff1);
        staffRepository.save(staff2);
        List<Staff> getStaff = staffRepository.findAll();
        assertThat(getStaff).isNotEqualTo(null);
        assertEquals(2, getStaff.size());
    }

    @Test
    void getStaffById(){
        Staff newStaff = staffRepository.save(staff1);
        Staff getStaff = staffRepository.findById(newStaff.getId()).get();
        assertNotNull(getStaff);
        assertThat(getStaff).isNotEqualTo(null);
        assertEquals("Israel", getStaff.getFirstName());
    }

    @Test
    void updateStaff(){
        Staff newStaff = staffRepository.save(staff2);
        Staff getStaff = staffRepository.findById(newStaff.getId()).get();
        getStaff.setGender("Male");
        getStaff.setDateOfBirth("27/10/2001");
        getStaff.setSalary(1000000.00);
        getStaff.setPhoneNumber("08136793904");
        getStaff.setStatus("ACTIVE");
        getStaff.setEnabled(true);
        getStaff.setFirstName("Israel");
        staffRepository.save(getStaff);

        assertNotNull(getStaff);
        assertEquals(newStaff.getId(), getStaff.getId());
        assertEquals("Israel", getStaff.getFirstName());
        assertEquals("08136793904", getStaff.getPhoneNumber());
    }

    @Test
    void deleteStaff(){
        Staff staff = staffRepository.save(staff1);
        staffRepository.deleteById(staff.getId());

        Optional<Staff> getStaff = staffRepository.findById(staff.getId());
        assertThat(getStaff).isEmpty();
    }

    @Test
    void addStaffAddress(){
        Staff newStaff = staffRepository.save(staff1);
        StaffAddress newAddress = addressRepository.save(staffAddress);
        assertNotNull(newAddress);
        assertThat(newAddress.getId()).isNotEqualTo(null);
        assertThat(newStaff.getId()).isEqualTo(newAddress.getStaff().getId());
    }

    @Test
    void getAddressByStaffId(){
        Staff newStaff = staffRepository.save(staff1);
        addressRepository.save(staffAddress);
        addressRepository.save(staffAddress2);

        List<StaffAddress> getAddress = addressRepository.findStaffAddressesByStaff_Id(newStaff.getId());
        assertNotNull(getAddress);
        assertThat(getAddress.size()).isEqualTo(2);

    }

    @Test
    void updateAddress(){
        staffRepository.save(staff1);
        StaffAddress address = addressRepository.save(staffAddress);
        Optional<StaffAddress> addressOptional = addressRepository.findById(address.getId());
        StaffAddress updateAddress = addressOptional.get();

        updateAddress.setCity("Gwagwalada");
        addressRepository.save(updateAddress);

        assertNotNull(updateAddress);
        assertEquals("Gwagwalada", updateAddress.getCity());
        assertEquals("FCT", updateAddress.getState());
    }

    @Test
    void deleteAddressById(){
        staffRepository.save(staff1);
        StaffAddress address = addressRepository.save(staffAddress);
        addressRepository.deleteById(address.getId());

        Optional<StaffAddress> addressOptional = addressRepository.findById(address.getId());
        assertThat(addressOptional).isEmpty();
    }

    @Test
    void deleteAddressByStaffId(){
        Staff staff = staffRepository.save(staff1);
        addressRepository.save(staffAddress);
        addressRepository.save(staffAddress2);

        List<StaffAddress> staffAddresses = addressRepository.findStaffAddressesByStaff_Id(staff.getId());
        for(StaffAddress address : staffAddresses){
            addressRepository.deleteById(address.getId());
        }

        List<StaffAddress> check = addressRepository.findStaffAddressesByStaff_Id(staff.getId());
        assertThat(check).isEmpty();
        assertEquals(0, check.size());
    }

    public StaffRequestDto mapToStaff1(){
        return StaffRequestDto.builder()
                .firstName("Israel")
                .lastName("Oni")
                .email("test@gmail.com")
                .password("pwd1")
                .roles(Roles.ADMIN)
                .build();
    }

    public StaffRequestDto mapToStaff2(){
        return StaffRequestDto.builder()
                .firstName("Okikijesu")
                .lastName("Oni")
                .email("test2@gmail.com")
                .password("pwd1")
                .roles(Roles.DOCTOR)
                .build();
    }

    public StaffAddressDTO mapToStaffAddress(){
        return StaffAddressDTO.builder()
                .state("FCT")
                .city("Abuja")
                .street("Phase 3 Zone 4")
                .country("Nigeria")
                .postalCode("902101")
                .build();
    }

    public StaffAddressDTO mapToStaffAddress2(){
        return StaffAddressDTO.builder()
                .state("Osun")
                .city("Iwo")
                .street("No. 3, Bowen Quarters")
                .country("Nigeria")
                .postalCode("902101")
                .build();
    }
}
