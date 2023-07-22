package com.clepbo.hospital_management_system.appointment.service;

import com.clepbo.hospital_management_system.appointment.dto.AppointmentRequestDTO;
import com.clepbo.hospital_management_system.appointment.dto.AppointmentResponseDTO;
import com.clepbo.hospital_management_system.appointment.entity.Appointment;
import com.clepbo.hospital_management_system.appointment.entity.Status;
import com.clepbo.hospital_management_system.appointment.repository.IAppointmentRepository;
import com.clepbo.hospital_management_system.patient.dto.RequestToSeeADoctorRequestDTO;
import com.clepbo.hospital_management_system.patient.entity.PatientBio;
import com.clepbo.hospital_management_system.patient.entity.RequestToSeeADoctor;
import com.clepbo.hospital_management_system.patient.repository.IPatientBioRepository;
import com.clepbo.hospital_management_system.patient.repository.IRequestToSeeADoctorRepository;
import com.clepbo.hospital_management_system.patient.service.IRequestToSeeADoctorService;
import com.clepbo.hospital_management_system.staff.dto.CustomResponse;
import com.clepbo.hospital_management_system.staff.entity.Staff;
import com.clepbo.hospital_management_system.staff.repository.IStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService{

    private final IAppointmentRepository appointmentRepository;
    private final IPatientBioRepository patientBioRepository;
    private final IStaffRepository staffRepository;
    private final IRequestToSeeADoctorRepository requestToSeeADoctorRepository;
    private final IRequestToSeeADoctorService requestToSeeADoctorService;

    @Override
    public ResponseEntity<CustomResponse> createAppointment(AppointmentRequestDTO requestDTO, Long staffId, Long patientId) {
        Optional<Staff> findStaff = staffRepository.findById(staffId);
        Optional<PatientBio> findPatient = patientBioRepository.findById(patientId);
        List<Appointment> confirmStaffIsFree = appointmentRepository.findAppointmentsByStaff_Id(staffId);
        List<Appointment> confirmPatientIsFree = appointmentRepository.findAppointmentsByPatientBios_Id(patientId);

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime appointmentDateTime = combineDateAndTime(requestDTO.date(), requestDTO.time());
        if(!findStaff.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Invalid StaffId " + staffId));
        }
        if(!findPatient.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Invalid PatientId " + patientId));
        }

        if(requestDTO.time() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Time cannot be empty"));
        }
        if(requestDTO.date() == null){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NO_CONTENT.name(), "Date cannot be empty"));
        }

        if(currentDate.compareTo(appointmentDateTime) < 0){
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
        PatientBio patientBio = findPatient.get();
        Appointment createAppointment = Appointment.builder()
                .date(requestDTO.date())
                .time(requestDTO.time())
                .description(requestDTO.description())
                .status(Status.PENDING)
                .staff(staff)
                .patientBios(patientBio)
                .build();
        appointmentRepository.save(createAppointment);
        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), "Appointment Created Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> createAppointmentByRequest(Long requestId, AppointmentRequestDTO requestDTO, Long staffId) {
        Optional<RequestToSeeADoctor> findRequest = requestToSeeADoctorRepository.findById(requestId);
        Optional<Staff> findStaff = staffRepository.findById(staffId);
        if(!findRequest.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Request not found " + requestId));
        }

        RequestToSeeADoctor request = findRequest.get();
        RequestToSeeADoctorRequestDTO requestToSeeADoctorRequestDTO = new RequestToSeeADoctorRequestDTO(request.getReason(), request.getPatientBio().getEmail());
        Staff staff = findStaff.get();
        Appointment createAppointment = Appointment.builder()
                .date(requestDTO.date())
                .time(requestDTO.time())
                .description(requestDTO.description())
                .status(Status.PENDING)
                .staff(staff)
                .patientBios(request.getPatientBio())
                .build();
        appointmentRepository.save(createAppointment);

        requestToSeeADoctorService.updateRequestStatus(requestId, requestToSeeADoctorRequestDTO, Status.FIXED.name());
        return ResponseEntity.ok(new CustomResponse(HttpStatus.CREATED.name(), "Appointment Created Successfully"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAllAppointment() {
        List<Appointment> getAllAppointment = appointmentRepository.findAll();
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
        return ResponseEntity.ok(new CustomResponse(HttpStatus.FOUND.name(), "Successful"));
    }

    @Override
    public ResponseEntity<CustomResponse> getAppointmentByStaffId(Long staffId) {
        Optional<Staff> findStaff = staffRepository.findById(staffId);
        if(!findStaff.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Staff not found"));
        }

        Staff staff = findStaff.get();
        List<Appointment> findStaffAppointment = appointmentRepository.findAppointmentsByStaff_Id(staff.getId());
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
    public ResponseEntity<CustomResponse> getAppointmentByPatientId(Long patientId) {
        Optional<PatientBio> findPatient = patientBioRepository.findById(patientId);
        if(!findPatient.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Patient not found"));
        }

        PatientBio patientBio = findPatient.get();
        List<Appointment> findPatientAppointment = appointmentRepository.findAppointmentsByPatientBios_Id(patientBio.getId());
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
    public ResponseEntity<CustomResponse> getAppointmentByDate(LocalDate date) {
        List<Appointment> findAppointmentByDate = appointmentRepository.findAppointmentsByDate(date);
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
    public ResponseEntity<CustomResponse> rescheduleAppointment(Long appointmentId, LocalDate date, LocalTime time) {
        Optional<Appointment> findAppointment = appointmentRepository.findById(appointmentId);

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime appointmentDateTime = combineDateAndTime(date, time);

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

        if(currentDate.isBefore(appointmentDateTime)){
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
        rescheduledAppointment.setTime(time);
        rescheduledAppointment.setStatus(Status.RESCHEDULED);
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
        LocalDateTime appointmentDateTime = combineDateAndTime(requestDTO.date(), requestDTO.time());

        if(!findAppointment.isPresent()){
            return ResponseEntity.badRequest().body(new CustomResponse(HttpStatus.NOT_FOUND.name(), "Appointment not found"));
        }

        Appointment updatedAppointment = findAppointment.get();
        List<Appointment> confirmStaffIsFree = appointmentRepository.findAppointmentsByStaff_Id(updatedAppointment.getStaff().getId());
        List<Appointment> confirmPatientIsFree = appointmentRepository.findAppointmentsByPatientBios_Id(updatedAppointment.getPatientBios().getId());

        if(currentDate.isBefore(appointmentDateTime)){
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

    private LocalDateTime combineDateAndTime(LocalDate date, LocalTime time) {
        return date.atTime(time);
    }
}
