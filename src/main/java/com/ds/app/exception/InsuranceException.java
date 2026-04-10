package com.ds.app.exception;

//Root exception for the entire Insurance Module
public class InsuranceException extends RuntimeException {
    public InsuranceException(String message) {
        super(message);
    }
}