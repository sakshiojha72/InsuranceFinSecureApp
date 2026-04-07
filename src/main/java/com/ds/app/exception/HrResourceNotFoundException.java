package com.ds.app.exception;

import org.springframework.http.HttpStatus;

// Use when: employee, company, department, project, escalation not found in DB
// HR prefix ensures no name clash when merging with other modules
public class HrResourceNotFoundException extends HrException {

    public HrResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id,
              HttpStatus.NOT_FOUND, "HR_RESOURCE_NOT_FOUND");
    }

    public HrResourceNotFoundException(String resource, String detail) {
        super(resource + " not found: " + detail,
              HttpStatus.NOT_FOUND, "HR_RESOURCE_NOT_FOUND");
    }
}
