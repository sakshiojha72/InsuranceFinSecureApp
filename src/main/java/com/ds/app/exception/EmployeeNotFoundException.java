package com.ds.app.exception;

public class EmployeeNotFoundException extends ResourceNotFoundException {
    public EmployeeNotFoundException(Long employeeId) {
        super("Employee not found with ID: " + employeeId);
    }
}