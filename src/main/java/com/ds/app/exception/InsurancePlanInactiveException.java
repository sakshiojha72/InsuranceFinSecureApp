package com.ds.app.exception;

public class InsurancePlanInactiveException extends InvalidStateException {
    public InsurancePlanInactiveException(Long planId) {
        super("Insurance plan with ID " + planId +
              " is inactive and cannot be assigned to employees.");
    }
}