package com.ds.app.exception;

//Base for invalid state errors
	////e.g. trying to assign an inactive plan, claiming on expired insurance

public class InvalidStateException extends InsuranceException {
 public InvalidStateException(String message) {
     super(message);
 }
}