package com.ds.app.exception;

//Base for all "entity not found in DB" scenarios
public class ResourceNotFoundException extends InsuranceException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}