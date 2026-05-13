package com.ds.app.entity;

public enum InsuranceStatus {
    ACTIVE,          // policy is valid, more than 30 days left
    EXPIRING_SOON,   // NEW expires within 30 days — show yellow warning
    EXPIRED          // policy has passed its expiry date — show red
}