package com.ds.app.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// Stub for Timesheet module API

@Component
public class TimesheetServiceClient {

	
	
//	 @Autowired  private ITimesheetRepository timesheetRepository;
//
//	    public boolean hasActiveTimesheet(Long employeeId) {
//	        int currentMonth = LocalDate.now().getMonthValue();  // e.g. 6 for June
//	        int currentYear  = LocalDate.now().getYear();        // e.g. 2025
//
//	        // uses Timesheet team's repository method directly
//	        return timesheetRepository.existsByEmployeeUserIdAndMonthAndYear(
//	                employeeId, currentMonth, currentYear);
//	    }

    public boolean hasActiveTimesheet(Long long1) {
       
        return true; 
    }
}

