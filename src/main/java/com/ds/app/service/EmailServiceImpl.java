package com.ds.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Async
public class EmailServiceImpl {

    @Autowired private JavaMailSender mailSender;

    private void send(String to, String subject, String body) {
       
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            msg.setFrom("hr@finsecure.com");
            mailSender.send(msg);
      
    }

    public void sendAllocationEmail(String to, String name,
                                     String company, String dept, String project) {
        send(to, "FinSecure — You have been assigned",
             "Dear " + name + ",\n\n"
             + "You have been assigned to:\n"
             + "  Company    : " + company + "\n"
             + "  Department : " + dept + "\n"
             + "  Project    : " + (project != null ? project : "Not assigned yet") + "\n\n"
             + "Regards,\nFinSecure HR Team");
    }

    public void sendDeallocationEmail(String to, String name, String type) {
        send(to, "FinSecure — Allocation Update",
             "Dear " + name + ",\n\n"
             + "Your " + type.toLowerCase() + " allocation has been removed.\n"
             + "Please contact HR for further information.\n\n"
             + "Regards,\nFinSecure HR Team");
    }

    public void sendEscalationEmail(String to, String name) {
        send(to, "FinSecure — Escalation Notice",
             "Dear " + name + ",\n\n"
             + "An escalation has been raised on your profile.\n"
             + "Please log in to your dashboard for details.\n\n"
             + "Regards,\nFinSecure HR Team");
    }

    public void sendAppraisalEmail(String to, String name, Double newSalary) {
        send(to, "FinSecure — Salary Appraisal Completed",
             "Dear " + name + ",\n\n"
             + "Your yearly appraisal is complete.\n"
             + "Updated salary: " + newSalary + "\n\n"
             + "Regards,\nFinSecure HR Team");
    }
    
    
 
    public void sendStaleEscalationAlert(String to, Long escalationId,
                                          String targetName, String targetEmail,
                                          long daysOpen) {
        send(to,
             "FinSecure — URGENT: Escalation SLA Breached (#" + escalationId + ")",
             "This is an automated alert.\n\n"
             + "Escalation #" + escalationId + " against " + targetName
             + " (" + targetEmail + ") has been OPEN for " + daysOpen + " days "
             + "without action.\n\n"
             + "SLA requirement: Escalations must be moved to IN_PROGRESS within 7 days.\n\n"
             + "Please review immediately.\n\n"
             + "Regards,\nFinSecure Automated Alert System"
        );
    }


}

