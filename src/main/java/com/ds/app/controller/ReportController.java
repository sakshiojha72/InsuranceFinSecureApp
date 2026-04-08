package com.ds.app.controller;


import com.ds.app.service.ReportServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/finsecure/hr")
public class ReportController {

    @Autowired private ReportServiceImpl reportService;

    // all report endpoints — ADMIN view only, HR full access
    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/reports/summary")
    public ResponseEntity<Map<String, Long>> getSummary() {
        return ResponseEntity.ok(reportService.getSummary());
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/reports/group/company")
    public ResponseEntity<Map<String, Long>> groupByCompany() {
        return ResponseEntity.ok(reportService.countGroupByCompany());
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/reports/group/department")
    public ResponseEntity<Map<String, Long>> groupByDepartment() {
        return ResponseEntity.ok(reportService.countGroupByDepartment());
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/reports/group/type")
    public ResponseEntity<Map<String, Long>> groupByType() {
        return ResponseEntity.ok(reportService.countGroupByEmployeeType());
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/reports/group/status")
    public ResponseEntity<Map<String, Long>> groupByStatus() {
        return ResponseEntity.ok(reportService.countGroupByStatus());
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/reports/count/company/{companyId}")
    public ResponseEntity<Long> countByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(reportService.countByCompany(companyId));
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/reports/count/department/{deptId}")
    public ResponseEntity<Long> countByDepartment(@PathVariable Long deptId) {
        return ResponseEntity.ok(reportService.countByDepartment(deptId));
    }
    
    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/reports/business")
    public ResponseEntity<Map<String, Map<String, List<String>>>> getCompanyPerspectiveReport() {
       
        return ResponseEntity.ok(reportService.getCompanyPerspectiveReport());
    }
}

