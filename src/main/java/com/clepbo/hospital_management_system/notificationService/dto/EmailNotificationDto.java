package com.clepbo.hospital_management_system.notificationService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationDto {
    private String recipientEmail;
    private String header;
    private String reservationCode;
    private String appointmentDate;
    private String appointmentTime;
    private String doctor;
    private String patient;

}
