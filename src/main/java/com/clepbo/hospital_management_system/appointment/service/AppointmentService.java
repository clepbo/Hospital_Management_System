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
    private final IRequestToSeeADoctorService requestToSeeADoctorService;
    private final IMailService mailService;

    @Override
    public ResponseEntity<CustomResponse> createAppointment(AppointmentRequestDTO requestDTO) {
        Optional<Staff> findStaff = staffRepository.findById(Long.valueOf(requestDTO.staffId()));
        Optional<PatientBio> findPatient = patientBioRepository.findById(Long.valueOf(requestDTO.patientId()));
        List<Appointment> confirmStaffIsFree = appointmentRepository.findAppointmentsByStaff_Id(Long.valueOf(requestDTO.staffId()));
        List<Appointment> confirmPatientIsFree = appointmentRepository.findAppointmentsByPatientBios_Id(Long.valueOf(requestDTO.patientId()));

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime appointmentDateTime = combineDateAndTime(requestDTO.date(), LocalTime.parse(requestDTO.time()));
        if(!findStaff.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Invalid StaffId " + requestDTO.staffId()));
        }
        if(!findPatient.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Invalid PatientId " + requestDTO.patientId()));
        }

        if(requestDTO.time() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Time cannot be empty"));
        }
        if(requestDTO.date() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Date cannot be empty"));
        }
        if(appointmentDateTime.isBefore(currentDate)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Invalid date or time"));
        }

        for(Appointment checkSchedule : confirmStaffIsFree){
            LocalDateTime staffAppointmentDateTime = combineDateAndTime(checkSchedule.getDate(), checkSchedule.getTime());
            if(staffAppointmentDateTime.isEqual(appointmentDateTime)
                    && (checkSchedule.getStatus().name().equalsIgnoreCase("PENDING")
                    || checkSchedule.getStatus().name().equalsIgnoreCase("RESCHEDULED"))){
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Staff is not free for the scheduled time"));
            }
        }

        for(Appointment checkSchedule : confirmPatientIsFree){
            LocalDateTime patientAppointmentDateTime = combineDateAndTime(checkSchedule.getDate(), checkSchedule.getTime());
            if(patientAppointmentDateTime.isEqual(appointmentDateTime)
                    && (checkSchedule.getStatus().name().equalsIgnoreCase("PENDING")
                    || checkSchedule.getStatus().name().equalsIgnoreCase("RESCHEDULED"))){
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Patient is not free for the scheduled time"));
            }
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

        //send mail to patient
        mailService.notifyPatient(new EmailNotificationDto(
                subject,
                patientBio.getEmail(),
                header,
                createAppointment.getReservationCode(),
                createAppointment.getDate().toString(),
                createAppointment.getTime().toString(),
                "Dr. " + staff.getFirstName() + " " + staff.getLastName(),
                patientBio.getFirstname()  + " " + patientBio.getLastname()
        ));

        //send mail to doctor
        mailService.notifyDoctor(new EmailNotificationDto(
                subject,
                staff.getEmail(),
                header,
                createAppointment.getReservationCode(),
                createAppointment.getDate().toString(),
                createAppointment.getTime().toString(),
                "Dr. " + staff.getFirstName() + " " + staff.getLastName(),
                patientBio.getFirstname() + " " + patientBio.getLastname()));
        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), "Appointment Created Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> createAppointmentByRequest(Long requestId, AppointmentRequestDTO requestDTO) {
        Optional<RequestToSeeADoctor> findRequest = requestToSeeADoctorRepository.findById(requestId);
        Optional<Staff> findStaff = staffRepository.findById(Long.valueOf(requestDTO.staffId()));

        List<Appointment> confirmStaffIsFree = appointmentRepository.findAppointmentsByStaff_Id(Long.valueOf(requestDTO.staffId()));
        List<Appointment> confirmPatientIsFree = appointmentRepository.findAppointmentsByPatientBios_Id(findRequest.get().getPatientBio().getId());

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime appointmentDateTime = combineDateAndTime(requestDTO.date(), LocalTime.parse(requestDTO.time()));
        if(!findRequest.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Request not found " + requestId));
        }

        if(appointmentDateTime.isBefore(currentDate)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Invalid date or time"));
        }

        for(Appointment checkSchedule : confirmStaffIsFree){
            LocalDateTime staffAppointmentDateTime = combineDateAndTime(checkSchedule.getDate(), checkSchedule.getTime());
            if(staffAppointmentDateTime.isEqual(appointmentDateTime)
                    && (checkSchedule.getStatus().name().equalsIgnoreCase("PENDING")
                    || checkSchedule.getStatus().name().equalsIgnoreCase("RESCHEDULED"))){
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Staff is not free for the scheduled time"));
            }
        }

        for(Appointment checkSchedule : confirmPatientIsFree){
            LocalDateTime patientAppointmentDateTime = combineDateAndTime(checkSchedule.getDate(), checkSchedule.getTime());
            if(patientAppointmentDateTime.isEqual(appointmentDateTime)
                    && (checkSchedule.getStatus().name().equalsIgnoreCase("PENDING")
                    || checkSchedule.getStatus().name().equalsIgnoreCase("RESCHEDULED"))){
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Patient is not free for the scheduled time"));
            }
        }

        RequestToSeeADoctor request = findRequest.get();
        if(requestDTO.patientId().toString().equals(request.getPatientBio().getId())){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Invalid Patient Id"));
        }

        RequestToSeeADoctorRequestDTO requestToSeeADoctorRequestDTO = new RequestToSeeADoctorRequestDTO(request.getReason(), request.getPatientBio().getEmail());
        Staff staff = findStaff.get();

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

        //send main to patient
        mailService.notifyPatient(new EmailNotificationDto(
                subject,
                request.getPatientBio().getEmail(),
                header,
                createAppointment.getReservationCode(),
                createAppointment.getDate().toString(),
                createAppointment.getTime().toString(),
                "Dr. " + staff.getFirstName() + " " + staff.getLastName(),
                request.getPatientBio().getFirstname() + " " + request.getPatientBio().getLastname()));

        //send mail to doctor
        mailService.notifyDoctor(new EmailNotificationDto(
                subject,
                staff.getEmail(),
                header,
                createAppointment.getReservationCode(),
                createAppointment.getDate().toString(),
                createAppointment.getTime().toString(),
                "Dr. " + staff.getFirstName() + " " + staff.getLastName(),
                request.getPatientBio().getFirstname() + " " + request.getPatientBio().getLastname()));

        appointmentRepository.save(createAppointment);

        requestToSeeADoctorService.updateRequestStatus(requestId, requestToSeeADoctorRequestDTO, Status.FIXED.name());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), "Appointment Created Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAllAppointment(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Appointment> getAllAppointment = appointmentRepository.findAll(pageable);
        List<AppointmentResponseDTO> responseDTOS = getAllAppointment.stream()
                .map(appointment -> AppointmentResponseDTO.builder()
                        .id(appointment.getId())
                        .status(String.valueOf(appointment.getStatus()))
                        .patientName(appointment.getPatientBios().getFirstname() +" "+ appointment.getPatientBios().getLastname())
                        .doctorName(appointment.getStaff().getFirstName() +" "+ appointment.getStaff().getLastName())
                        .date(appointment.getDate())
                        .time(appointment.getTime())
                        .description(appointment.getDescription())
                        .build())
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
        AppointmentResponseDTO responseDTO = AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .date(appointment.getDate())
                .time(appointment.getTime())
                .doctorName(appointment.getStaff().getFirstName() +" "+ appointment.getStaff().getLastName())
                .patientName(appointment.getPatientBios().getFirstname() +" "+ appointment.getPatientBios().getLastname())
                .description(appointment.getDescription())
                .status(String.valueOf(appointment.getStatus()))
                .build();
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
                .map(appointment -> AppointmentResponseDTO.builder()
                        .id(appointment.getId())
                        .status(String.valueOf(appointment.getStatus()))
                        .patientName(appointment.getPatientBios().getFirstname() +" "+ appointment.getPatientBios().getLastname())
                        .doctorName(appointment.getStaff().getFirstName() +" "+ appointment.getStaff().getLastName())
                        .date(appointment.getDate())
                        .time(appointment.getTime())
                        .description(appointment.getDescription())
                        .build())
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
                .map(appointment -> AppointmentResponseDTO.builder()
                        .id(appointment.getId())
                        .status(String.valueOf(appointment.getStatus()))
                        .patientName(appointment.getPatientBios().getFirstname() +" "+ appointment.getPatientBios().getLastname())
                        .doctorName(appointment.getStaff().getFirstName() +" "+ appointment.getStaff().getLastName())
                        .date(appointment.getDate())
                        .time(appointment.getTime())
                        .description(appointment.getDescription())
                        .build())
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
                .map(appointment -> AppointmentResponseDTO.builder()
                        .id(appointment.getId())
                        .status(String.valueOf(appointment.getStatus()))
                        .patientName(appointment.getPatientBios().getFirstname() +" "+ appointment.getPatientBios().getLastname())
                        .doctorName(appointment.getStaff().getFirstName() +" "+ appointment.getStaff().getLastName())
                        .date(appointment.getDate())
                        .time(appointment.getTime())
                        .description(appointment.getDescription())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), responseDTOS, "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> rescheduleAppointment(Long appointmentId, LocalDate date, String time) {
        Optional<Appointment> findAppointment = appointmentRepository.findById(appointmentId);

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime appointmentDateTime = combineDateAndTime(date, LocalTime.parse(time));

        if(!findAppointment.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Appointment not found"));
        }
        if(date == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Date cannot be empty"));
        }
        if(time == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Time cannot be empty"));
        }

        Appointment rescheduledAppointment = findAppointment.get();
        List<Appointment> confirmStaffIsFree = appointmentRepository.findAppointmentsByStaff_Id(rescheduledAppointment.getStaff().getId());
        List<Appointment> confirmPatientIsFree = appointmentRepository.findAppointmentsByPatientBios_Id(rescheduledAppointment.getPatientBios().getId());

        if(rescheduledAppointment.getStatus().name().equals("ATTENDED_TO")){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Cannot reschedule this appointment"));
        }
        if(appointmentDateTime.isBefore(currentDate)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Invalid date or time"));
        }

        for(Appointment checkSchedule : confirmStaffIsFree){
            LocalDateTime staffAppointmentDateTime = combineDateAndTime(checkSchedule.getDate(), checkSchedule.getTime());
            if(staffAppointmentDateTime.isEqual(appointmentDateTime)
                    && (checkSchedule.getStatus().name().equalsIgnoreCase("PENDING")
                    || checkSchedule.getStatus().name().equalsIgnoreCase("RESCHEDULED"))){
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Staff is not free for the scheduled time"));
            }
        }

        for(Appointment checkSchedule : confirmPatientIsFree){
            LocalDateTime patientAppointmentDateTime = combineDateAndTime(checkSchedule.getDate(), checkSchedule.getTime());
            if(patientAppointmentDateTime.isEqual(appointmentDateTime)
                    && (checkSchedule.getStatus().name().equalsIgnoreCase("PENDING")
                    || checkSchedule.getStatus().name().equalsIgnoreCase("RESCHEDULED"))){
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Patient is not free for the scheduled time"));
            }
        }

        rescheduledAppointment.setDate(date);
        rescheduledAppointment.setTime(LocalTime.parse(time));
        rescheduledAppointment.setStatus(Status.RESCHEDULED);
        String header = "Your appointment has been rescheduled!";
        String subject = "APPOINTMENT RESCHEDULED";

        //send mail to patient
        mailService.notifyPatient(new EmailNotificationDto(
                subject,
                rescheduledAppointment.getPatientBios().getEmail(),
                header,
                rescheduledAppointment.getReservationCode(),
                rescheduledAppointment.getDate().toString(),
                rescheduledAppointment.getTime().toString(),
                "Dr. " + rescheduledAppointment.getStaff().getFirstName() + " " + rescheduledAppointment.getStaff().getLastName(),
                rescheduledAppointment.getPatientBios().getFirstname() + " " + rescheduledAppointment.getPatientBios().getLastname()
        ));

        //send mail to doctor
        mailService.notifyDoctor(new EmailNotificationDto(
                subject,
                rescheduledAppointment.getStaff().getEmail(),
                header,
                rescheduledAppointment.getReservationCode(),
                rescheduledAppointment.getDate().toString(),
                rescheduledAppointment.getTime().toString(),
                "Dr. " + rescheduledAppointment.getStaff().getFirstName() + " " + rescheduledAppointment.getStaff().getLastName(),
                rescheduledAppointment.getPatientBios().getFirstname() + " " + rescheduledAppointment.getPatientBios().getLastname()
        ));

        appointmentRepository.save(rescheduledAppointment);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Appointment Rescheduled Successfully"));
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
                return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Appointment status updated successfully"));
            }
        }
        return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Invalid Status"));
    }

    @Override
    public ResponseEntity<CustomResponse> updateAppointment(Long appointmentId, AppointmentRequestDTO requestDTO) {
        Optional<Appointment> findAppointment = appointmentRepository.findById(appointmentId);

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime appointmentDateTime = combineDateAndTime(requestDTO.date(), LocalTime.parse(requestDTO.time()));

        if(!findAppointment.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Appointment not found"));
        }

        Appointment updatedAppointment = findAppointment.get();
        List<Appointment> confirmStaffIsFree = appointmentRepository.findAppointmentsByStaff_Id(updatedAppointment.getStaff().getId());
        List<Appointment> confirmPatientIsFree = appointmentRepository.findAppointmentsByPatientBios_Id(updatedAppointment.getPatientBios().getId());

        if(appointmentDateTime.isBefore(currentDate)){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Invalid date or time"));
        }

        for(Appointment checkSchedule : confirmStaffIsFree){
            LocalDateTime staffAppointmentDateTime = combineDateAndTime(checkSchedule.getDate(), checkSchedule.getTime());
            if(staffAppointmentDateTime.isEqual(appointmentDateTime)
                    && (checkSchedule.getStatus().name().equalsIgnoreCase("PENDING")
                    || checkSchedule.getStatus().name().equalsIgnoreCase("RESCHEDULED"))){
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Staff is not free for the scheduled time"));
            }
        }

        for(Appointment checkSchedule : confirmPatientIsFree){
            LocalDateTime patientAppointmentDateTime = combineDateAndTime(checkSchedule.getDate(), checkSchedule.getTime());
            if(patientAppointmentDateTime.isEqual(appointmentDateTime)
                    && (checkSchedule.getStatus().name().equalsIgnoreCase("PENDING")
                    || checkSchedule.getStatus().name().equalsIgnoreCase("RESCHEDULED"))){
                return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_ACCEPTABLE.name(), "Patient is not free for the scheduled time"));
            }
        }
        updatedAppointment.setStatus(Status.RESCHEDULED);
        updatedAppointment.setTime(LocalTime.parse(requestDTO.time()));

        BeanUtils.copyProperties(requestDTO, updatedAppointment, getNullPropertyNames(requestDTO));
        appointmentRepository.save(updatedAppointment);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.OK.name(), "Appointment updated successfully"));
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
}
