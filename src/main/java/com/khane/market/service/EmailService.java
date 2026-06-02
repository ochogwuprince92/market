package com.khane.market.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from:noreply@market.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4174}")
    private String frontendUrl;

    public void sendVerificationEmail(String toEmail, String verificationToken) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Email Verification - Market");
            message.setText(
                    "Hello,\n\n" +
                            "Thank you for registering. Please verify your email by clicking the link below:\n\n" +
                            verificationUrl + "\n\n" +
                            "This link expires in 24 hours.\n\n" +
                            "If you did not create an account, please ignore this email."
            );

            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset - Market");
            message.setText(
                    "Hello,\n\n" +
                            "You requested a password reset. Click the link below:\n\n" +
                            resetUrl + "\n\n" +
                            "This link expires in 1 hour.\n\n" +
                            "If you did not request this, please ignore this email."
            );

            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }
}