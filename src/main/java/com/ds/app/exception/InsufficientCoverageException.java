package com.ds.app.exception;

public class InsufficientCoverageException extends BusinessRuleException {
    public InsufficientCoverageException(Double claimAmount, Double remainingCoverage) {
        super("Claim amount ₹" + claimAmount +
              " exceeds remaining coverage of ₹" + remainingCoverage +
              ". Please reduce claim amount or purchase a top-up.");
    }
}