package com.clepbo.hospital_management_system.staff.service;

import com.clepbo.hospital_management_system.security.CustomUserDetails;
import com.clepbo.hospital_management_system.security.JwtService;
import com.clepbo.hospital_management_system.staff.dto.*;
import com.clepbo.hospital_management_system.staff.entity.Staff;
import com.clepbo.hospital_management_system.staff.entity.StaffAddress;
import com.clepbo.hospital_management_system.staff.repository.IStaffAddressRepository;
import com.clepbo.hospital_management_system.staff.repository.IStaffRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffService implements IStaffService{
    private final IStaffRepository staffRepository;
    private final IStaffAddressRepository staffAddressRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private static final String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$";
    private static final Pattern PATTERN = Pattern.compile(emailRegex);
    @Override
    public ResponseEntity<CustomResponse> createNewStaff(StaffRequestDto request) {
        if(request.firstName()==null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "firstName is required"));
        }
        if(request.lastName()==null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "lastName is required"));
        }
        if(request.email()==null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "email is required"));
        }
        if(!isEmailValid(request.email())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "provide correct email format"));
        }
        if(request.password()==null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "password is required"));
        }

        Optional<Staff> existingStaff = staffRepository.findByEmail(request.email());
        if(existingStaff.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "User with is email is taken."));
        }

        Staff staff = Staff.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .roles(request.roles())
                .password(passwordEncoder.encode(request.password()))
                .isEnabled(false)
                .build();
        staffRepository.save(staff);

        var accessToken = jwtService.generateToken(new CustomUserDetails(staff));

        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), accessToken, "Successfully created new staff"));
    }

    @Override
    public ResponseEntity<CustomResponse> authenticateStaff(StaffLoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getEmail(),
                loginRequestDTO.getPassword()
        ));
        Staff staff = staffRepository.findByEmail(loginRequestDTO.getEmail()).get();
        var accessToken = jwtService.generateToken(new CustomUserDetails(staff));

        StaffLoginResponseDTO responseDto = StaffLoginResponseDTO.builder()
                .accessToken(accessToken)
                .id(staff.getId())
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .email(staff.getEmail())
                .roles(staff.getRoles().name())
                .build();

        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDto, "Login Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAllStaff(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<Staff> staffs = staffRepository.findAll(pageable);
        List<StaffResponseDto> responseDtoList = staffs.stream()
                .map(this::mapToStaffResponse)
                .collect(Collectors.toList());



        CustomResponse customResponse = CustomResponse.builder()
                .status(HttpStatus.OK.name())
                .message("Successful")
                .data(responseDtoList.isEmpty() ? null : responseDtoList)
                .build();
        return ResponseEntity.ok(customResponse);
    }

    private StaffResponseDto mapToStaffResponse(Staff staff) {
        return StaffResponseDto.builder()
                .id(staff.getId())
                .email(staff.getEmail())
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .roles(staff.getRoles().name())
                .build();
    }

    @Override
    public ResponseEntity<CustomResponse> findStaffById(Long id) {
        Optional<Staff> getStaff = staffRepository.findById(id);
        if(getStaff.isPresent()){
            Staff staff = getStaff.get();
            StaffResponseDto responseDto = StaffResponseDto.builder()
                    .id(staff.getId())
                    .firstName(staff.getFirstName())
                    .lastName(staff.getLastName())
                    .email(staff.getEmail())
                    .build();
            return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDto, "Successful"));
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<CustomResponse> updateStaff(StaffBioDataRequestDto requestDto, Long id) {
        Optional<Staff> staffOptional = staffRepository.findById(id);
        staffOptional.orElseThrow(() -> new UsernameNotFoundException("Staff not found"));
        Staff updatedStaff = staffOptional.get();

        // Perform the partial update
        BeanUtils.copyProperties(requestDto, updatedStaff, getNullPropertyNames(requestDto));
        staffRepository.save(updatedStaff);


        return ResponseEntity.ok(new CustomResponse(HttpStatus.ACCEPTED.name(), "Update Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> deleteStaff(Long id) {
        Optional<Staff> getStaff = staffRepository.findById(id);
        if(getStaff.isPresent()){
            deleteStaffAddressByStaffId(id);
            staffRepository.deleteById(id);
            return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Staff Deleted Successfully"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Staff Address doesn't exist"));
    }

    @Override
    public ResponseEntity<CustomResponse> addStaffAddress(Long staffId, StaffAddressDTO request) {
        Optional<Staff> staffOpt = staffRepository.findById(staffId);
        if(!staffOpt.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "Staff doesn't exist"));
        }
        if(staffId == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "StaffID is required"));
        }
        if(request==null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "request body is required"));
        }

        StaffAddress staffAddress = StaffAddress.builder()
                .city(request.getCity())
                .street(request.getStreet())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .staff(staffOpt.get())
                .build();
        staffAddressRepository.save(staffAddress);
        if(staffAddress!=null){
            return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), "Successfully added address"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "Could not add address"));
    }

    @Override
    public ResponseEntity<CustomResponse> getStaffAddressByStaffId(Long staffId) {
        Optional<Staff> findStaff = staffRepository.findById(staffId);
        if(findStaff.isPresent()){
            List<StaffAddress> staffAddressList = staffAddressRepository.findStaffAddressesByStaff_Id(staffId);
            if(staffAddressList.isEmpty()){
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Staff Address is empty"));
            }
            List<StaffAddressDTO> staffAddresses = staffAddressList.stream()
                    .map(staffAddress -> StaffAddressDTO.builder()
                            .street(staffAddress.getStreet())
                            .state(staffAddress.getState())
                            .city(staffAddress.getCity())
                            .postalCode(staffAddress.getPostalCode())
                            .country(staffAddress.getCountry())
                            .build())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), staffAddresses, "Staff Address found"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Invalid staffId " + staffId));
    }

    @Override
    public ResponseEntity<CustomResponse> updateStaffAddress(Long staffId, StaffAddressDTO addressDTO, Long addressId) {
        Optional<Staff> findStaffByStaffId = staffRepository.findById(staffId);
        if(findStaffByStaffId.isPresent()){
            Staff staffExist = findStaffByStaffId.get();
            Optional<StaffAddress> findByAddressId = staffAddressRepository.findStaffAddressByIdAndStaff_Id(addressId, staffExist.getId());
            if(findByAddressId.isPresent()){
                StaffAddress updateAddress = findByAddressId.get();
                BeanUtils.copyProperties(addressDTO, updateAddress, getNullPropertyNames(updateAddress));
                staffAddressRepository.save(updateAddress);
                return ResponseEntity.ok(new CustomResponse(HttpStatus.ACCEPTED.name(), updateAddress, "Address update Successful"));
            }
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "Invalid Address Id"));
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<CustomResponse> deleteStaffAddress(Long addressId) {
        Optional<StaffAddress> staffAddress = staffAddressRepository.findById(addressId);
        if(staffAddress.isPresent()){
            staffAddressRepository.deleteById(addressId);
            return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Staff Address successfully deleted"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Staff Address doesn't exist"));
    }

    @Override
    public ResponseEntity<CustomResponse> deleteStaffAddressByStaffId(Long staffId) {
        Optional<Staff> findStaff = staffRepository.findById(staffId);
        if(findStaff.isPresent()){
            List<StaffAddress> allStaffAddress = staffAddressRepository.findStaffAddressesByStaff_Id(staffId);
            for(StaffAddress address:allStaffAddress){
                staffAddressRepository.deleteById(address.getId());
            }
            return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Staff Address deleted Successfully"));
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Staff Address doesn't exist"));
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

    public static boolean isEmailValid(String email){
       Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }

    @PostConstruct
    public void init() {
        StaffLoginRequestDTO loginRequestDTO = StaffLoginRequestDTO.builder()
                .email("admin@login.com")
                .password("admin")
                .build();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getEmail(),
                loginRequestDTO.getPassword()
        ));
        Staff staff = staffRepository.findByEmail(loginRequestDTO.getEmail()).get();
        var accessToken = jwtService.generateToken(new CustomUserDetails(staff));

        log.info("\n\nADMIN_TOKEN: " + accessToken + "\n\n");
    }

}
