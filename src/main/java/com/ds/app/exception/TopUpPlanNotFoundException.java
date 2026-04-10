package com.ds.app.exception;

public class TopUpPlanNotFoundException extends ResourceNotFoundException {
    public TopUpPlanNotFoundException(Long topUpPlanId) {
        super("Top-up plan not found or inactive with ID: " + topUpPlanId);
    }
}