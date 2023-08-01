package com.clepbo.hospital_management_system.mailSender.service;

import com.clepbo.hospital_management_system.mailSender.dto.MailSender;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class MailService implements IMailService{

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final JavaMailSender javaMailSender;
    @Override
    public String sendMail(MailSender mailSender) {
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper((mimeMessage));

            mimeMessage.setFrom(senderEmail);
            mimeMessage.setRecipients(Message.RecipientType.TO, mailSender.getRecipientEmail());
            mimeMessage.setSubject(mailSender.getSubject());
            mimeMessage.setText(mailSender.getBody(), "ISO-8859-1", "html");

            javaMailSender.send(mimeMessage);
            return "Mail Sent Successfully";

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
