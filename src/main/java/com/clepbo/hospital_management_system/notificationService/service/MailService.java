package com.clepbo.hospital_management_system.notificationService.service;

import com.clepbo.hospital_management_system.notificationService.dto.EmailNotificationDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService implements IMailService{

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final JavaMailSender javaMailSender;
    @Override
    public void notifyPatient(EmailNotificationDto notificationDto) {
        try{
            String subject = "APPOINTMENT CONFIRMATION";
            String senderName = "Hospital Management System";

            // Read template content from the file
            ClassPathResource templateResource = new ClassPathResource("templates/patient-notification-template.html");
            String templateContent = new String(Files.readAllBytes(templateResource.getFile().toPath()));

            // Replace placeholders in the template content with dynamic values
            String mailContent = templateContent
                    .replace("[[time]]", notificationDto.getAppointmentTime())
                    .replace("[[patientName]]", notificationDto.getPatient())
                    .replace("[[doctorName]]", notificationDto.getDoctor())
                    .replace("[[header]]", notificationDto.getHeader())
                    .replace("[[reservationCode]]", notificationDto.getReservationCode())
                    .replace("[[date]]", notificationDto.getAppointmentDate()
                    );

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setFrom(senderEmail, senderName);
            messageHelper.setTo(notificationDto.getRecipientEmail());
            messageHelper.setSubject(subject);
            messageHelper.setText(mailContent, true);

            javaMailSender.send(message);

            log.info("Mail Sent Successfully");

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void notifyDoctor(EmailNotificationDto notificationDto) {
        try{
            String subject = "APPOINTMENT CONFIRMATION";
            String senderName = "Hospital Management System";

            // Read template content from the file
            ClassPathResource templateResource = new ClassPathResource("templates/doctor-notification-template.html");
            String templateContent = new String(Files.readAllBytes(templateResource.getFile().toPath()));

            // Replace placeholders in the template content with dynamic values
            String mailContent = templateContent
                    .replace("[[time]]", notificationDto.getAppointmentTime())
                    .replace("[[patientName]]", notificationDto.getPatient())
                    .replace("[[doctorName]]", notificationDto.getDoctor())
                    .replace("[[header]]", notificationDto.getHeader())
                    .replace("[[reservationCode]]", notificationDto.getReservationCode())
                    .replace("[[date]]", notificationDto.getAppointmentDate()
                    );

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setFrom(senderEmail, senderName);
            messageHelper.setTo(notificationDto.getRecipientEmail());
            messageHelper.setSubject(subject);
            messageHelper.setText(mailContent, true);

            javaMailSender.send(message);

            log.info("Mail Sent Successfully");

        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }

}
