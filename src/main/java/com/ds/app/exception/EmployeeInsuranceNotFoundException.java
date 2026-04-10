package com.ds.app.exception;

public class EmployeeInsuranceNotFoundException extends ResourceNotFoundException {
    public EmployeeInsuranceNotFoundException(Long id) {
        super("Employee insurance record not found with ID: " + id);
    }
    public EmployeeInsuranceNotFoundException(String message) {
        super(message);
    }
}