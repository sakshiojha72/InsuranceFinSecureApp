package com.ds.app.exception;

public class InsurancePlanNotFoundException extends ResourceNotFoundException {
    public InsurancePlanNotFoundException(Long planId) {
        super("Insurance plan not found with ID: " + planId);
    }
}