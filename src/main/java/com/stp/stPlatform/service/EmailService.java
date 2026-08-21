package com.stp.stPlatform.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

        String subject = "Verify your OTP - ST Platform";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #333; text-align: center;">Two-Factor Authentication</h2>
                <p>Hello,</p>
                <p>Use the one-time verification code below to complete your login request:</p>
                <div style="text-align: center; margin: 30px 0;">
                    <span style="font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #2563eb; background: #eff6ff; padding: 10px 24px; border-radius: 6px; display: inline-block;">
                        %s
                    </span>
                </div>
                <p style="color: #666; font-size: 14px;">This code is valid for 5 minutes. If you did not request this code, please secure your account immediately.</p>
            </div>
            """.formatted(otp);

        try {
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlContent, true);
            mimeMessageHelper.setTo(email);
            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new MessagingException("Failed to send OTP email: " + e.getMessage(), e);
        }
    }
}