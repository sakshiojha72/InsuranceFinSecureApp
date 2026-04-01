package com.ds.app.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.response.InsuranceSummaryDTO;
import com.ds.app.entity.Employee;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.InsuranceSummaryService;

@RestController
@RequestMapping("/finsecure/insurance/summary")
public class InsuranceSummaryController {

    @Autowired
    private InsuranceSummaryService insuranceSummaryService;

    @Autowired
    private EmployeeRepository employeeRepository;

    // only EMPLOYEE can view their own summary
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<InsuranceSummaryDTO> getMySummary(
            Principal principal) {

        Employee employee = employeeRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        InsuranceSummaryDTO summary =
            insuranceSummaryService.getInsuranceSummary(employee.getUserId());

        return ResponseEntity.ok(summary);
    }

    // ADMIN and HR can view any employee's summary
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{employeeId}")
    public ResponseEntity<InsuranceSummaryDTO> getEmployeeSummary(
            @PathVariable Long employeeId) {

        InsuranceSummaryDTO summary =
            insuranceSummaryService.getInsuranceSummary(employeeId);

        return ResponseEntity.ok(summary);
    }
}