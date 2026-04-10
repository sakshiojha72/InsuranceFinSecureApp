package com.ds.app.exception;

//Base for all business logic violations
		//// e.g. claim > coverage, duplicate assignment, already processed

public class BusinessRuleException extends InsuranceException {
    public BusinessRuleException(String message) {
        super(message);
    }
}