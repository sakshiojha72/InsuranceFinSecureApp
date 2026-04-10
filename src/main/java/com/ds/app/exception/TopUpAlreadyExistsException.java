package com.ds.app.exception;

public class TopUpAlreadyExistsException extends BusinessRuleException {
    public TopUpAlreadyExistsException(Long topUpPlanId) {
        super("You already have an active top-up for plan ID: " + topUpPlanId +
              ". You cannot purchase the same top-up twice.");
    }
}