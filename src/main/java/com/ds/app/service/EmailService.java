package com.ds.app.service;

import com.ds.app.controller.AdminController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

// Handles all email notifications for the Insurance Module

@Service
public class EmailService {

    private final AdminController adminController;

    @Autowired
    private JavaMailSender mailSender;

    EmailService(AdminController adminController) {
        this.adminController = adminController;
    }

    // ── PRIVATE HELPER — sends the actual email ───────────────────────────
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
    
 // ─────NEW CHANGES(5TH MAY)───── PLAN DEACTIVATED + AUTO-REASSIGNED ───────────────────────────────────
    public void sendPlanDeactivatedAndReassignedEmail(
    		String toEmail,
    		String employeeName,
    		String oldPlanName,
    		String newPlanName,
    		Double newCoverageAmount,
    		String newExpiryDate)
    {
        String subject = "Your Insurance Plan Has Been Updated — FinSecure";
        String body = "Dear " + employeeName + ",\n\n"
            + "Your current insurance plan \"" + oldPlanName + "\" has been deactivated "
            + "by the administrator.\n\n"
            + "You have been automatically enrolled in our default plan:\n\n"
            + "  New Plan Name  : " + newPlanName + "\n"
            + "  Coverage Amount: ₹" + newCoverageAmount + "\n"
            + "  Valid Until    : " + newExpiryDate + "\n\n"
            + "No action is required from your side. Your coverage continues uninterrupted.\n"
            + "If you have questions about your new plan, please contact HR.\n\n"
            + "Regards,\nFinSecure HR Team";

        sendEmail(toEmail, subject, body);
    	
    }
}