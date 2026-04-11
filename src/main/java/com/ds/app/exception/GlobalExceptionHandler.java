package com.ds.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── NOT FOUND EXCEPTIONS (404) ────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(
            ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<String> handleEmployeeNotFound(
            EmployeeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InsurancePlanNotFoundException.class)
    public ResponseEntity<String> handlePlanNotFound(
            InsurancePlanNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(EmployeeInsuranceNotFoundException.class)
    public ResponseEntity<String> handleInsuranceNotFound(
            EmployeeInsuranceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(TopUpPlanNotFoundException.class)
    public ResponseEntity<String> handleTopUpNotFound(
            TopUpPlanNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // ── CONFLICT EXCEPTIONS (409) ─────────────────────────────────────────────

    @ExceptionHandler(InsuranceAlreadyAssignedException.class)
    public ResponseEntity<String> handleAlreadyAssigned(
            InsuranceAlreadyAssignedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ClaimAlreadyProcessedException.class)
    public ResponseEntity<String> handleAlreadyProcessed(
            ClaimAlreadyProcessedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // ── BUSINESS RULE EXCEPTIONS (422) ────────────────────────────────────────

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<String> handleBusinessRule(
            BusinessRuleException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientCoverageException.class)
    public ResponseEntity<String> handleInsufficientCoverage(
            InsufficientCoverageException ex) {
        // 422 — request was valid but violates business rule
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }

    // ── BAD REQUEST EXCEPTIONS (400) ──────────────────────────────────────────

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<String> handleInvalidState(
            InvalidStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InsuranceException.class)
    public ResponseEntity<String> handleInsuranceException(
            InsuranceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // ── VALIDATION ERRORS (@Valid on request DTOs) (400) ──────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // each field that failed validation gets its own entry
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // ── ACCESS DENIED (403) ───────────────────────────────────────────────────

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(
            AuthorizationDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body("Access Denied - you do not have permission");
    }

    // ── GENERIC RUNTIME (400) ─────────────────────────────────────────────────

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // ── CATCH ALL (500) ───────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}