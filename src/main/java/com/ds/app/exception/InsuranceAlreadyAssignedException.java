package com.ds.app.exception;

public class InsuranceAlreadyAssignedException extends BusinessRuleException {
    public InsuranceAlreadyAssignedException(Long employeeId) {
        super("Employee with ID " + employeeId +
              " already has an active insurance. Deactivate it before assigning a new one.");
    }
}