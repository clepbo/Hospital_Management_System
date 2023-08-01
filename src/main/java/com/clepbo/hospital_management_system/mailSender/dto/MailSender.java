package com.clepbo.hospital_management_system.mailSender.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailSender {
    private String recipientEmail;
    private String subject;
    private String body;
}
