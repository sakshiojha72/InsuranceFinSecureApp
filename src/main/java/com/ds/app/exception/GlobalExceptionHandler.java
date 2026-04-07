package com.ds.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles validation errors when @Valid fails on request bodies(Bhawna)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "HR_VALIDATION_FAILED", message);
    }

    // Standard 404 handler for when a specific resource isn't in the database(Bhawna)
    @ExceptionHandler(HrResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(HrResourceNotFoundException ex) {
        return build(ex.getStatus(), ex.getErrorCode(), ex.getMessage());
    }

    // Handles logic-specific errors defined by our business rules(Bhawna)
    @ExceptionHandler(HrBusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRule(HrBusinessRuleException ex) {
        return build(ex.getStatus(), ex.getErrorCode(), ex.getMessage());
    }

    // Prevents duplicate entries (e.g., trying to use an existing employee code)(Bhawna)
    @ExceptionHandler(HrDuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(HrDuplicateResourceException ex) {
        return build(ex.getStatus(), ex.getErrorCode(), ex.getMessage());
    }

    // General fallback for our custom HrException hierarchy(Bhawna)
    @ExceptionHandler(HrException.class)
    public ResponseEntity<Map<String, Object>> handleHrException(HrException ex) {
        return build(ex.getStatus(), ex.getErrorCode(), ex.getMessage());
    }

    // Catch-all for Spring Security role/authority check failures(Bhawna)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "HR_ACCESS_DENIED",
                "You don't have the right permissions to do this.");
    }

    // Debugging helper: Pinpoints exactly where a NullPointerException happened in our code(Bhawna)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointer(NullPointerException ex) {
        String location = "unknown";
        if (ex.getStackTrace() != null) {
            for (StackTraceElement el : ex.getStackTrace()) {
                if (el.getClassName().startsWith("com.ds.app")) {
                    location = el.getClassName().substring(el.getClassName().lastIndexOf('.') + 1)
                               + "." + el.getMethodName()
                               + " (line " + el.getLineNumber() + ")";
                    break;
                }
            }
        }
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "HR_NULL_VALUE",
                "Something went wrong because a value was missing at: " + location);
    }

    // Triggered when a path variable or request param is the wrong type (e.g., text instead of a number)(Bhawna)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        String expected = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName() : "valid value";
        return build(HttpStatus.BAD_REQUEST, "HR_INVALID_INPUT",
                "The value '" + ex.getValue() + "' isn't right. We were expecting a " + expected);
    }

    // Catch-all for when a required @RequestParam is missing from the URL(Bhawna)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(
            MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, "HR_MISSING_PARAMETER",
                "The required parameter '" + ex.getParameterName() + "' is missing.");
    }

    // Final safety net for any unhandled or unexpected system errors(Bhawna)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "HR_INTERNAL_ERROR",
                "Something went wrong on our end. Please try again later.");
    }

    // Helper method to keep error responses looking consistent across the app(Bhawna)
    private ResponseEntity<Map<String, Object>> build(
            HttpStatus status, String errorCode, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("errorCode", errorCode);
        body.put("message",   message);
        return new ResponseEntity<>(body, status);
    }
}
