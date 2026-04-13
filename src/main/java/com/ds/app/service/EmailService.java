package com.ds.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// Handles all email notifications for the Insurance Module
// Uses Spring Boot's built-in JavaMailSender — no third party libraries
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ── PRIVATE HELPER — sends the actual email ───────────────────────────
    // All public methods below call this internally
    // SimpleMailMessage = plain text email, no HTML needed
    private void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("Email sent to: " + toEmail);
    }

    // ── CLAIM APPROVED ────────────────────────────────────────────────────
    public void sendClaimApprovedEmail(String toEmail, String employeeName,
            Double claimAmount, String planName, String adminRemarks) {
        String subject = "Your Insurance Claim Has Been Approved — FinSecure";
        String body = "Dear " + employeeName + ",\n\n"
            + "Your insurance claim has been APPROVED.\n\n"
            + "Details:\n"
            + "  Plan        : " + planName + "\n"
            + "  Claim Amount: ₹" + claimAmount + "\n"
            + "  Remarks     : " + adminRemarks + "\n\n"
            + "The amount will be processed shortly.\n\n"
            + "Regards,\nFinSecure HR Team";
        sendEmail(toEmail, subject, body);
    }

    // ── CLAIM REJECTED ────────────────────────────────────────────────────
    public void sendClaimRejectedEmail(String toEmail, String employeeName,
            Double claimAmount, String planName, String adminRemarks) {
        String subject = "Your Insurance Claim Has Been Rejected — FinSecure";
        String body = "Dear " + employeeName + ",\n\n"
            + "Unfortunately, your insurance claim has been REJECTED.\n\n"
            + "Details:\n"
            + "  Plan        : " + planName + "\n"
            + "  Claim Amount: ₹" + claimAmount + "\n"
            + "  Reason      : " + adminRemarks + "\n\n"
            + "If you have questions, please contact HR.\n\n"
            + "Regards,\nFinSecure HR Team";
        sendEmail(toEmail, subject, body);
    }

    // ── INSURANCE ASSIGNED ────────────────────────────────────────────────
    public void sendInsuranceAssignedEmail(String toEmail, String employeeName,
            String planName, Double coverageAmount, String expiryDate) {
        String subject = "Insurance Plan Assigned — FinSecure";
        String body = "Dear " + employeeName + ",\n\n"
            + "An insurance plan has been assigned to you.\n\n"
            + "Details:\n"
            + "  Plan Name      : " + planName + "\n"
            + "  Coverage Amount: ₹" + coverageAmount + "\n"
            + "  Valid Until    : " + expiryDate + "\n\n"
            + "You can raise claims anytime through the FinSecure portal.\n\n"
            + "Regards,\nFinSecure HR Team";
        sendEmail(toEmail, subject, body);
    }
}