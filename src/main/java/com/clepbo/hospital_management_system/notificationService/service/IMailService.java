package com.clepbo.hospital_management_system.notificationService.service;

import com.clepbo.hospital_management_system.notificationService.dto.EmailNotificationDto;
import com.clepbo.hospital_management_system.notificationService.dto.RequestNotification;

public interface IMailService {
    void notifyPatient(EmailNotificationDto notificationDto);
    void notifyDoctor(EmailNotificationDto notificationDto);
    void patientRequest(RequestNotification requestNotification);
}
