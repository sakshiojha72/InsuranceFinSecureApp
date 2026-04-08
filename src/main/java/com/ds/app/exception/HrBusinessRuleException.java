package com.ds.app.exception;

import org.springframework.http.HttpStatus;

// Use when: business rule is violated
// Examples: training/timesheet/assets check fails before allocation
//           appraisal on already appraised employee in same year
public class HrBusinessRuleException extends HrException {

    public HrBusinessRuleException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "HR_BUSINESS_RULE_VIOLATION");
    }
}
