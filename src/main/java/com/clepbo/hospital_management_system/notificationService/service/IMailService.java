package com.clepbo.hospital_management_system.notificationService.service;

import com.clepbo.hospital_management_system.notificationService.dto.MailSender;

public interface IMailService {
    String sendMail(MailSender mailSender);
}
