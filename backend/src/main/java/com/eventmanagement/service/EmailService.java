package com.eventmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String to, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Welcome to Event Management System!");
            message.setText("Hi " + firstName + ",\n\n" +
                    "Welcome to our Event Management System!\n" +
                    "You can now browse and register for various events.\n\n" +
                    "Best regards,\n" +
                    "Event Management Team");
            message.setFrom("noreply@eventmanagement.com");

            mailSender.send(message);
            log.info("Welcome email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending welcome email to: {}", to, e);
        }
    }

    public void sendRegistrationConfirmation(String to, String firstName, String eventTitle) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Event Registration Confirmation");
            message.setText("Hi " + firstName + ",\n\n" +
                    "Thank you for registering for: " + eventTitle + "\n" +
                    "Your registration is confirmed.\n" +
                    "We will send you a reminder before the event.\n\n" +
                    "Best regards,\n" +
                    "Event Management Team");
            message.setFrom("noreply@eventmanagement.com");

            mailSender.send(message);
            log.info("Registration confirmation email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending registration confirmation email to: {}", to, e);
        }
    }

    public void sendEventReminder(String to, String firstName, String eventTitle, String eventDate) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Reminder: " + eventTitle);
            message.setText("Hi " + firstName + ",\n\n" +
                    "This is a reminder about the upcoming event:\n" +
                    "Event: " + eventTitle + "\n" +
                    "Date: " + eventDate + "\n" +
                    "Please make sure to attend!\n\n" +
                    "Best regards,\n" +
                    "Event Management Team");
            message.setFrom("noreply@eventmanagement.com");

            mailSender.send(message);
            log.info("Event reminder sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending event reminder to: {}", to, e);
        }
    }
}
