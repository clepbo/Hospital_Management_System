package com.clepbo.hospital_management_system.appointment.service;

import com.clepbo.hospital_management_system.appointment.dto.AppointmentRequestDTO;
import com.clepbo.hospital_management_system.appointment.dto.AppointmentResponseDTO;
import com.clepbo.hospital_management_system.appointment.entity.Appointment;
import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.appointment.repository.IAppointmentRepository;
import com.clepbo.hospital_management_system.notificationService.dto.EmailNotificationDto;
import com.clepbo.hospital_management_system.notificationService.service.IMailService;
import com.clepbo.hospital_management_system.patient.dto.RequestToSeeADoctorRequestDTO;
import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.entity.RequestToSeeADoctor;
import com.clepbo.hospital_management_system.patient.repository.IPatientBioRepository;
import com.clepbo.hospital_management_system.patient.repository.IRequestToSeeADoctorRepository;
import com.clepbo.hospital_management_system.patient.service.IRequestToSeeADoctorService;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import com.clepbo.hospital_management_system.staff.entity.Roles;
import com.clepbo.hospital_management_system.staff.entity.Staff;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService implements IAppointmentService{

    private final IAppointmentRepository appointmentRepository;
    private final IPatientBioRepository patientBioRepository;
    private final IStaffRepository staffRepository;
    private final IRequestToSeeADoctorRepository requestToSeeADoctorRepository;
    private final IMailService mailService;

    @Override
    public ResponseEntity<CustomResponse> createAppointment(AppointmentRequestDTO requestDTO) {
        if(!validateRequestFields(requestDTO)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "One or more field is empty or equals 'string'"));
        }

        Optional<Staff> findStaff = staffRepository.findById(Long.valueOf(requestDTO.staffId()));
        Optional<PatientBio> findPatient = patientBioRepository.findById(Long.valueOf(requestDTO.patientId()));

        List<String> messages = new ArrayList<>();

        if(findStaff.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Invalid StaffId " + requestDTO.staffId()));
        }
        if(findPatient.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Invalid PatientId " + requestDTO.patientId()));
        }

        if(!checkIfStaffAndPatientAreFree(requestDTO, messages)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), messages.toString()));
        }

        Staff staff = findStaff.get();

        if(!staff.getRoles().equals(Roles.DOCTOR)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Cannot book appointment with Staff " + staff.getId()));
        }
        PatientBio patientBio = findPatient.get();
        Appointment createAppointment = Appointment.builder()
                .date(requestDTO.date())
                .time(LocalTime.parse(requestDTO.time()))
                .description(requestDTO.description())
                .status(Status.PENDING)
                .staff(staff)
                .patientBios(patientBio)
                .reservationCode("#" + generateUniqueRandom())
                .build();
        appointmentRepository.save(createAppointment);

        String header = "Your reservation has been confirmed!";
        String subject = "APPOINTMENT CONFIRMATION";
        sendNotification(createAppointment, patientBio, staff, header, subject);

        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), createAppointment, "Appointment Created Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> createAppointmentByRequest(Long requestId, AppointmentRequestDTO requestDTO) {
        if(!validateRequestFields(requestDTO)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.BAD_REQUEST.name(), "One or more field is empty or equals 'string'"));
        }

        Optional<RequestToSeeADoctor> findRequest = requestToSeeADoctorRepository.findById(requestId);
        Optional<Staff> findStaff = staffRepository.findById(Long.valueOf(requestDTO.staffId()));
        Optional<PatientBio> findPatient = patientBioRepository.findById(Long.valueOf(requestDTO.patientId()));

        List<String> messages = new ArrayList<>();

        if(findRequest.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Request not found " + requestId));
        }

        if(!checkIfStaffAndPatientAreFree(requestDTO, messages)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), messages.toString()));
        }

        RequestToSeeADoctor request = findRequest.get();
        if(requestDTO.patientId().toString().equals(request.getPatientBio().getId())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Invalid Patient Id"));
        }

        Staff staff = findStaff.get();
        PatientBio patientBio = findPatient.get();

        if(!staff.getRoles().equals(Roles.DOCTOR)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Cannot book appointment with Staff " + staff.getId()));
        }

        Appointment createAppointment = Appointment.builder()
                .date(requestDTO.date())
                .time(LocalTime.parse(requestDTO.time()))
                .description(requestDTO.description())
                .status(Status.PENDING)
                .staff(staff)
                .patientBios(request.getPatientBio())
                .reservationCode("#" + generateUniqueRandom())
                .build();

        String header = "Your reservation has been confirmed!";
        String subject = "APPOINTMENT CONFIRMATION";

        sendNotification(createAppointment, patientBio, staff, header, subject);

        request.setStatus(Status.FIXED);
        requestToSeeADoctorRepository.save(request);
        appointmentRepository.save(createAppointment);

        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), createAppointment, "Appointment Created Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAllAppointment(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Appointment> getAllAppointment = appointmentRepository.findAll(pageable);
        List<AppointmentResponseDTO> responseDTOS = getAllAppointment.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        if(getAllAppointment.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "List is empty"));
        }
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDTOS, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAppointmentById(Long appointmentId) {
        Optional<Appointment> findAppointment = appointmentRepository.findById(appointmentId);
        if(!findAppointment.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Appointment not found"));
        }

        Appointment appointment = findAppointment.get();
        AppointmentResponseDTO responseDTO = mapToResponseDTO(appointment);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), responseDTO, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAppointmentByStaffId(Long staffId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Optional<Staff> findStaff = staffRepository.findById(staffId);
        if(!findStaff.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Staff not found"));
        }

        Staff staff = findStaff.get();
        Page<Appointment> findStaffAppointment = appointmentRepository.findAppointmentsByStaff_Id(staff.getId(), pageable);
        if(findStaffAppointment.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "List is empty"));
        }

        List<AppointmentResponseDTO> responseDTOS = findStaffAppointment.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDTOS, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAppointmentByPatientId(Long patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Optional<PatientBio> findPatient = patientBioRepository.findById(patientId);
        if(!findPatient.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient not found"));
        }

        PatientBio patientBio = findPatient.get();
        Page<Appointment> findPatientAppointment = appointmentRepository.findAppointmentsByPatientBios_Id(patientBio.getId(), pageable);
        if(findPatientAppointment.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "List is empty"));
        }

        List<AppointmentResponseDTO> responseDTOS = findPatientAppointment.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDTOS, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAppointmentByDate(LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Appointment> findAppointmentByDate = appointmentRepository.findAppointmentsByDate(date, pageable);
        if(findAppointmentByDate.isEmpty()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "List is empty"));
        }

        List<AppointmentResponseDTO> responseDTOS = findAppointmentByDate.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDTOS, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> rescheduleAppointment(Long appointmentId, LocalDate date, String time) {
        Optional<Appointment> findAppointment = appointmentRepository.findById(appointmentId);

        if(!findAppointment.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Appointment not found"));
        }

        Appointment rescheduledAppointment = findAppointment.get();
        AppointmentRequestDTO requestDTO = AppointmentRequestDTO.builder()
                .time(time)
                .date(date)
                .patientId(rescheduledAppointment.getPatientBios().getId().toString())
                .staffId(rescheduledAppointment.getStaff().getId().toString())
                .description(rescheduledAppointment.getDescription())
                .build();

        if(rescheduledAppointment.getStatus().name().equals("ATTENDED_TO")){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Cannot reschedule this appointment"));
        }

        List<String> messages = new ArrayList<>();

        if(!checkIfStaffAndPatientAreFree(requestDTO, messages)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), messages.toString()));
        }

        rescheduledAppointment.setDate(date);
        rescheduledAppointment.setTime(LocalTime.parse(time));
        rescheduledAppointment.setStatus(Status.RESCHEDULED);
        String header = "Your appointment has been rescheduled!";
        String subject = "APPOINTMENT RESCHEDULED";

        sendNotification(rescheduledAppointment, rescheduledAppointment.getPatientBios(), rescheduledAppointment.getStaff(), header, subject);

        appointmentRepository.save(rescheduledAppointment);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), rescheduledAppointment, "Appointment Rescheduled Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> updateAppointmentStatus(Long appointmentId, String status) {
        Optional<Appointment> findAppointment = appointmentRepository.findById(appointmentId);
        if(!findAppointment.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Appointment not found"));
        }
        if(status == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Status cannot be empty"));
        }
        Appointment updatedStatus = findAppointment.get();

        if(LocalDate.now().isAfter(updatedStatus.getDate())){
            updatedStatus.setStatus(Status.EXPIRED);
        }
        for(Status statuses : Status.values()){
            if(statuses.name().equals(status.toUpperCase())){
                updatedStatus.setStatus(Status.valueOf(status.toUpperCase()));
                appointmentRepository.save(updatedStatus);
                return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), updatedStatus, "Appointment status updated successfully"));
            }
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Invalid Status"));
    }

    @Override
    public ResponseEntity<CustomResponse> updateAppointment(Long appointmentId, AppointmentRequestDTO requestDTO) {
        Optional<Appointment> findAppointment = appointmentRepository.findById(appointmentId);

        List<String> messages = new ArrayList<>();

        if(!findAppointment.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Appointment not found"));
        }

        if(!checkIfStaffAndPatientAreFree(requestDTO, messages)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), messages.toString()));
        }

        Appointment updatedAppointment = findAppointment.get();


        updatedAppointment.setStatus(Status.RESCHEDULED);
        updatedAppointment.setTime(LocalTime.parse(requestDTO.time()));

        BeanUtils.copyProperties(requestDTO, updatedAppointment, getNullPropertyNames(requestDTO));
        appointmentRepository.save(updatedAppointment);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), updatedAppointment, "Appointment updated successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> deleteAppointment(Long appointmentId) {
        Optional<Appointment> findAppointment = appointmentRepository.findById(appointmentId);
        if(!findAppointment.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Appointment not found"));
        }

        appointmentRepository.deleteById(appointmentId);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Appointment deleted successfully"));
    }

    @Override
    @Scheduled(cron = "0 0 * * * *")
    @PostConstruct
    public void expiredAppointment() {
        List<Appointment> findAllAppointment = appointmentRepository.findAll();
        if(!findAllAppointment.isEmpty()){
            for(Appointment appointment:findAllAppointment){
                LocalDateTime currentDate = LocalDateTime.now();
                LocalDateTime appointmentDateTime = combineDateAndTime(appointment.getDate(), appointment.getTime());
                if(currentDate.isAfter(appointmentDateTime) && (!appointment.getStatus().name().equals("EXPIRED")
                        || !appointment.getStatus().name().equals("CANCELLED") || !appointment.getStatus().name().equals("ATTENDED_TO"))){
                    appointment.setStatus(Status.EXPIRED);
                    appointmentRepository.save(appointment);
                }
            }
        }
    }

    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void notifyPatient(){
        List<Appointment> appointments = appointmentRepository.findAll();
        if(!appointments.isEmpty()){
            for(Appointment checkAppointment : appointments){
                LocalDateTime currentDate = LocalDateTime.now();
                LocalDateTime appointmentDateTime = combineDateAndTime(checkAppointment.getDate(), checkAppointment.getTime());
                long hoursDifference = ChronoUnit.HOURS.between(currentDate, appointmentDateTime);
                if(hoursDifference == 24){
                    String header = "A friendly reminder!";
                    String subject = "Don't Forget: Your Medical Appointment Tomorrow";

                    //send mail to patient
                    mailService.notifyPatient(new EmailNotificationDto(
                            subject,
                            checkAppointment.getPatientBios().getEmail(),
                            header,
                            checkAppointment.getReservationCode(),
                            checkAppointment.getDate().toString(),
                            checkAppointment.getTime().toString(),
                            "Dr. " + checkAppointment.getStaff().getFirstName() + " " + checkAppointment.getStaff().getLastName(),
                            checkAppointment.getPatientBios().getFirstname() + " " + checkAppointment.getPatientBios().getLastname()
                    ));

                    //send mail to doctor
                    mailService.notifyDoctor(new EmailNotificationDto(
                            subject,
                            checkAppointment.getStaff().getEmail(),
                            header,
                            checkAppointment.getReservationCode(),
                            checkAppointment.getDate().toString(),
                            checkAppointment.getTime().toString(),
                            "Dr. " + checkAppointment.getStaff().getFirstName() + " " + checkAppointment.getStaff().getLastName(),
                            checkAppointment.getPatientBios().getFirstname() + " " + checkAppointment.getPatientBios().getLastname()
                    ));
                }
            }
        }
    }

    private LocalDateTime combineDateAndTime(LocalDate date, LocalTime time) {
        return date.atTime(time);
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

    public static int generateUniqueRandom() {
        Set<Integer> generatedNumbers = new HashSet<>();
        Random random = new Random();

        while (generatedNumbers.size() < 4) {
            int randomNumber = random.nextInt(9000) + 1000; // Generate a number between 1000 and 9999
            generatedNumbers.add(randomNumber);
        }

        int uniqueNumber = generatedNumbers.iterator().next();
        return uniqueNumber;
    }

    public boolean checkIfStaffAndPatientAreFree(AppointmentRequestDTO requestDTO, List<String> messages){
        List<Appointment> confirmStaffIsFree = appointmentRepository.findAppointmentsByStaff_Id(Long.valueOf(requestDTO.staffId()));
        List<Appointment> confirmPatientIsFree = appointmentRepository.findAppointmentsByPatientBios_Id(Long.valueOf(requestDTO.patientId()));

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime appointmentDateTime = combineDateAndTime(requestDTO.date(), LocalTime.parse(requestDTO.time()));

        if(requestDTO.time() == null){
            messages.add("Time cannot be empty");
            return false;
        }

        if(requestDTO.date() == null){
            messages.add("Date cannot be empty");
            return false;
        }

        if(appointmentDateTime.isBefore(currentDate)){
            messages.add("Invalid date or time");
            return false;
        }

        for(Appointment checkSchedule : confirmStaffIsFree){
            LocalDateTime staffAppointmentDateTime = combineDateAndTime(checkSchedule.getDate(), checkSchedule.getTime());
            if(staffAppointmentDateTime.isEqual(appointmentDateTime)
                    && (checkSchedule.getStatus().name().equalsIgnoreCase("PENDING")
                    || checkSchedule.getStatus().name().equalsIgnoreCase("RESCHEDULED"))){
                messages.add("Staff is not free for the scheduled time");
                return false;
            }
        }

        for(Appointment checkSchedule : confirmPatientIsFree){
            LocalDateTime patientAppointmentDateTime = combineDateAndTime(checkSchedule.getDate(), checkSchedule.getTime());
            if(patientAppointmentDateTime.isEqual(appointmentDateTime)
                    && (checkSchedule.getStatus().name().equalsIgnoreCase("PENDING")
                    || checkSchedule.getStatus().name().equalsIgnoreCase("RESCHEDULED"))){
                messages.add("Patient is not free for the scheduled time");
                return false;
            }
        }

        return true;
    }

    public void sendNotification (Appointment appointment, PatientBio patientBio, Staff staff, String header, String subject){

        //send mail to patient
        mailService.notifyPatient(new EmailNotificationDto(
                subject,
                patientBio.getEmail(),
                header,
                appointment.getReservationCode(),
                appointment.getDate().toString(),
                appointment.getTime().toString(),
                "Dr. " + staff.getFirstName() + " " + staff.getLastName(),
                patientBio.getFirstname()  + " " + patientBio.getLastname()
        ));

        //send mail to doctor
        mailService.notifyDoctor(new EmailNotificationDto(
                subject,
                staff.getEmail(),
                header,
                appointment.getReservationCode(),
                appointment.getDate().toString(),
                appointment.getTime().toString(),
                "Dr. " + staff.getFirstName() + " " + staff.getLastName(),
                patientBio.getFirstname() + " " + patientBio.getLastname()));
    }

    public AppointmentResponseDTO mapToResponseDTO(Appointment appointment){
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .status(String.valueOf(appointment.getStatus()))
                .patientName(appointment.getPatientBios().getFirstname() +" "+ appointment.getPatientBios().getLastname())
                .doctorName(appointment.getStaff().getFirstName() +" "+ appointment.getStaff().getLastName())
                .date(appointment.getDate())
                .time(appointment.getTime())
                .description(appointment.getDescription())
                .build();
    }

    public static <T> boolean validateRequestFields(T requestDTO) {
        if (requestDTO == null) {
            return false; // Handle null input gracefully
        }

        // Get all fields in the request DTO class
        Field[] fields = requestDTO.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(requestDTO);

                if ((fieldValue != null && fieldValue.toString().equals("string")) || Objects.equals(fieldValue, "")) {
                    return false; // Found a field with value "String"
                }
            } catch (IllegalAccessException e) {
                // Handle any exceptions if needed
                e.printStackTrace();
            }
        }

        return true; // All fields are valid
    }
}
