//package com.ds.app.exception;
//
//// Custom exception for all HR module business rule violations
//// Caught by GlobalExceptionHandler and returned as clean JSON
//
////NOTE: keep the Exception as extends Exception(checked)
//public class HrException extends RuntimeException {
//
//    public HrException(String message) {
//        super(message);
//    }
//}


package com.ds.app.exception;

import org.springframework.http.HttpStatus;

// Base exception for all HR module errors
// All sub-exceptions start with "Hr" prefix — safe for merging
// Other modules can have their own base: AssetsException, TimesheetException etc.
public class HrException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public HrException(String message) {
        // simple constructor — used in services currently
        super(message);
        this.status    = HttpStatus.BAD_REQUEST;
        this.errorCode = "HR_ERROR";
    }

    public HrException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status    = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus()  { return status; }
    public String getErrorCode()   { return errorCode; }
}
