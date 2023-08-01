package com.clepbo.hospital_management_system.mailSender.service;

import com.clepbo.hospital_management_system.mailSender.dto.MailSender;

public interface IMailService {
    String sendMail(MailSender mailSender);
}
