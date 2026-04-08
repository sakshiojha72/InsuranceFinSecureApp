package com.ds.app.exception;

import org.springframework.http.HttpStatus;

// Use when: trying to create something that already exists
// Examples: company code already in DB
//           department code already exists in same company
public class HrDuplicateResourceException extends HrException {

    public HrDuplicateResourceException(String resource, String field, String value) {
        super(resource + " with " + field + " '" + value + "' already exists",
              HttpStatus.CONFLICT, "HR_DUPLICATE_RESOURCE");
    }
}

