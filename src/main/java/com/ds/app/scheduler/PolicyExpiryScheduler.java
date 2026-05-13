package com.ds.app.scheduler;

import com.ds.app.entity.EmployeeInsurance;
import com.ds.app.entity.InsuranceStatus;
import com.ds.app.repository.EmployeeInsuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class PolicyExpiryScheduler {

  
    private static final int EXPIRY_ALERT_DAYS = 30;

 
    @Autowired
    private EmployeeInsuranceRepository employeeInsuranceRepository;
    // a cron expression  a shorthand for "when to run."
    // Cron format: "second  minute  hour  day-of-month  month  day-of-week"
    //               0       0       0     *             *      *

    //@Scheduled(cron = "0 0 0 * * *")     //run at 00:00:00 every day = MIDNIGHT DAILY

    @Scheduled(cron ="${policy.expiry.scheduler.cron}")
    public void updatePolicyExpiryStatuses() {

        System.out.println("[PolicyExpiryScheduler] Running at: " + LocalDate.now());

        // get every EmployeeInsurance row from the database.
        List<EmployeeInsurance> allInsurances = employeeInsuranceRepository.findAll();

  
        LocalDate today = LocalDate.now();

        //  Calculate the alert threshold date (jissey for everydate gap calculate hoga)
        LocalDate alertThreshold = today.plusDays(EXPIRY_ALERT_DAYS);

        //counters 
        int expiredCount = 0;
        int expiringSoonCount = 0;
        int activeCount = 0;

        
        for (EmployeeInsurance insurance : allInsurances) {
        	
            LocalDate expiryDate = insurance.getExpiryDate();
            if (expiryDate == null) {
                continue;
            }
            //what the status SHOULD be.
            InsuranceStatus newStatus;

            if (expiryDate.isBefore(today)) {
                newStatus = InsuranceStatus.EXPIRED;
                expiredCount++;

            } else if (!expiryDate.isAfter(alertThreshold)) {
                newStatus = InsuranceStatus.EXPIRING_SOON;
                expiringSoonCount++;

            } else {
                newStatus = InsuranceStatus.ACTIVE;
                activeCount++;
            }

            // only update the DB if change
            if (!insurance.getStatus().equals(newStatus)) {
                insurance.setStatus(newStatus);

                employeeInsuranceRepository.save(insurance);
            }
        }


        System.out.println("[PolicyExpiryScheduler] Done.");
        System.out.println("  ACTIVE: " + activeCount);
        System.out.println("  EXPIRING_SOON: " + expiringSoonCount);
        System.out.println("  EXPIRED: " + expiredCount);
    }
}