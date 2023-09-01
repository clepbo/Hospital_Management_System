package com.clepbo.hospital_management_system.notificationService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestNotification {
    private String subject;
    private String recipientEmail;
    private String patientName;
}
