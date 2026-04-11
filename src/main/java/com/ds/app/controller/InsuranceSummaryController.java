package com.ds.app.controller;

import com.ds.app.dto.response.InsuranceSummaryDTO;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.service.InsuranceSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Insurance Summary", description = "Full coverage summary for an employee")
@RestController
@RequestMapping("/finsecure/insurance/summary")
public class InsuranceSummaryController {

    @Autowired
    private InsuranceSummaryService insuranceSummaryService;

    // only EMPLOYEE can view their own summary
    @Operation(summary = "My insurance summary", description = "EMPLOYEE only. Returns base coverage, top-up coverage, approved claims total and remaining coverage in one response.")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<InsuranceSummaryDTO> getMySummary() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long employeeId = userDetails.getUser().getUserId();
        InsuranceSummaryDTO summary = insuranceSummaryService.getInsuranceSummary(employeeId);
        return ResponseEntity.ok(summary);
    }

    // ADMIN and HR can view any employee's summary
    @Operation(summary = "Any employee's summary", description = "ADMIN and HR. Returns complete insurance summary for a given employee ID.")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    @GetMapping("/{employeeId}")
    public ResponseEntity<InsuranceSummaryDTO> getEmployeeSummary(
            @PathVariable Long employeeId) {
        InsuranceSummaryDTO summary = insuranceSummaryService.getInsuranceSummary(employeeId);
        return ResponseEntity.ok(summary);
    }
}