package com.ds.app.controller;

import com.ds.app.dto.response.ClaimResponseDTO;
import com.ds.app.dto.response.EmployeeInsuranceResponseDTO;
import com.ds.app.service.InsuranceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/finsecure/insurance/reports")
public class InsuranceReportController {

    @Autowired
    private InsuranceReportService insuranceReportService;


 // Query 1 — employees with insurance AND top-up
    @GetMapping("/with-topup")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<List<EmployeeInsuranceResponseDTO>> getWithTopUp(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
            insuranceReportService.getEmployeesWithInsuranceAndTopUp(page, size)
        );
    }


    // Query 2 — employees with insurance but NO top-up
    @GetMapping("/no-topup")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<List<EmployeeInsuranceResponseDTO>> getWithNoTopUp() {

        return ResponseEntity.ok(
            insuranceReportService.getEmployeesWithNoTopUp()
        );
    }


    // Query 3a — insurances assigned between two custom dates
    // @DateTimeFormat tells Spring how to parse the date string from the URL
    @GetMapping("/assigned-between")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<List<EmployeeInsuranceResponseDTO>> getAssignedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(
            insuranceReportService.getInsurancesAssignedBetween(startDate, endDate)
        );
    }


    // Query 3b — current Indian financial year
    @GetMapping("/current-financial-year")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<List<EmployeeInsuranceResponseDTO>> getCurrentFinancialYear() {

        return ResponseEntity.ok(
            insuranceReportService.getInsurancesForCurrentFinancialYear()
        );
    }


 // Query 4 — pending claims
    @GetMapping("/pending-claims")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<List<ClaimResponseDTO>> getPendingClaims(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
            insuranceReportService.getPendingClaims(page, size)
        );
    }


    // Query 5 — expiring soon, client passes how many days
    @GetMapping("/expiring-soon")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<List<EmployeeInsuranceResponseDTO>> getExpiringSoon(
            @RequestParam(defaultValue = "30") int days) {
            // defaultValue = "30" means if client doesn't pass ?days=, it defaults to 30

        return ResponseEntity.ok(
            insuranceReportService.getInsurancesExpiringSoon(days)
        );
    }
}