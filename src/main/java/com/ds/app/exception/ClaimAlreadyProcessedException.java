package com.ds.app.exception;

public class ClaimAlreadyProcessedException extends BusinessRuleException {
    public ClaimAlreadyProcessedException(Long claimId) {
        super("Claim with ID " + claimId + " has already been processed and cannot be modified.");
    }
    public ClaimAlreadyProcessedException(String message) {
        super(message);
    }
}