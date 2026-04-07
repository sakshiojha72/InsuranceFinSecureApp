package com.ds.app.service;


import com.ds.app.entity.Escalation;
import com.ds.app.enums.EscalationStatus;
import com.ds.app.repository.iEscalationRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;


// Runs daily at 9 AM
// Finds any OPEN escalation that has not been moved to IN_PROGRESS
// within 7 days — sends alert email to HR
@Service
public class EscalationSchedulerService {


    @Autowired private iEscalationRepository escalationRepo;
    @Autowired private EmailServiceImpl emailService;


    // cron = second minute hour day month weekday
    // "0 0 9 * * *" = every day at 9:00:00 AM
    @Scheduled(cron = "0 0 9 * * *")//0 0 9
    @Transactional
    public void alertOnStaleEscalations() {


        // any escalation still OPEN after 7 days is stale
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1); //7


        List<Escalation> staleEscalations = escalationRepo
                .findByStatusAndRaisedAtBefore(EscalationStatus.OPEN, cutoff);


        if (staleEscalations.isEmpty()) {
            System.out.println("[Scheduler] No stale escalations found");
            return;
        }


        System.out.println("[Scheduler] Found " + staleEscalations.size()
                + " stale escalations — sending alerts");


        for (Escalation esc : staleEscalations) {
            String targetName = esc.getTargetEmployee().getFirstName();
            String targetEmail = esc.getTargetEmployee().getEmail();
            long daysOpen = java.time.temporal.ChronoUnit.DAYS.between(
                    esc.getRaisedAt(), LocalDateTime.now());

            
            // alert HR inbox
            emailService.sendStaleEscalationAlert(
                    "bhavvvvvs@gmail.com",
                    esc.getId(),
                    targetName,
                    targetEmail,
                    daysOpen
            );


            System.out.println("[Scheduler] Alert sent for escalation #"
                    + esc.getId() + " — open for " + daysOpen + " days");
        }
    }
}
