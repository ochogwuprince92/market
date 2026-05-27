package com.khane.market.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${app.email.from:noreply@market.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * Send email verification link
     */
    public void sendVerificationEmail(String toEmail, String verificationToken) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;
        String subject = "Email Verification - Market";
        String body = "Click the link below to verify your email:\n\n" + verificationUrl + "\n\nThis link expires in 24 hours.";

        try {
            // In production, integrate with SendGrid, AWS SES, or similar
            log.info("Verification email sent to: {} with URL: {}", toEmail, verificationUrl);
            // sendEmailViaSMTP(toEmail, subject, body);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}", toEmail, e);
        }
    }

    /**
     * Send password reset link
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
        String subject = "Password Reset - Market";
        String body = "Click the link below to reset your password:\n\n" + resetUrl + "\n\nThis link expires in 1 hour. If you did not request this, ignore this email.";

        try {
            // In production, integrate with SendGrid, AWS SES, or similar
            log.info("Password reset email sent to: {} with URL: {}", toEmail, resetUrl);
            // sendEmailViaSMTP(toEmail, subject, body);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}", toEmail, e);
        }
    }

    // Helper method for actual email sending (implement with your email provider)
    // private void sendEmailViaSMTP(String to, String subject, String body) { ... }
}

