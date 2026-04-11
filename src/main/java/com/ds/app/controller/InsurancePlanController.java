package com.ds.app.controller;

import com.ds.app.dto.request.AssignInsuranceRequestDTO;
import com.ds.app.dto.request.CreateInsurancePlanRequestDTO;
import com.ds.app.dto.response.EmployeeInsuranceResponseDTO;
import com.ds.app.dto.response.InsurancePlanResponseDTO;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.service.InsurancePlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/finsecure/insurance/plans")
public class InsurancePlanController {

    @Autowired
    private InsurancePlanService insurancePlanService;

    // only ADMIN can create a plan
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<InsurancePlanResponseDTO> createInsurancePlan(
            @Valid @RequestBody CreateInsurancePlanRequestDTO dto) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUser().getUsername();
        InsurancePlanResponseDTO response =
            insurancePlanService.createInsurancePlan(dto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ADMIN and HR can view all plans
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    @GetMapping
    public ResponseEntity<List<InsurancePlanResponseDTO>> getAllPlans() {
        List<InsurancePlanResponseDTO> plans =
            insurancePlanService.getAllInsurancePlans();
        return ResponseEntity.ok(plans);
    }

    // only ADMIN can deactivate a plan
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{planId}")
    public ResponseEntity<String> deactivatePlan(
            @PathVariable Long planId) {
        insurancePlanService.deactivateInsurancePlan(planId);
        return ResponseEntity.ok("Insurance plan deactivated successfully");
    }

    // only ADMIN can assign a plan to an employee
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/assign")
    public ResponseEntity<EmployeeInsuranceResponseDTO> assignInsurance(
            @Valid @RequestBody AssignInsuranceRequestDTO dto) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        String assignedBy = userDetails.getUser().getUsername();
        EmployeeInsuranceResponseDTO response =
            insurancePlanService.assignInsurance(dto, assignedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // only EMPLOYEE can view their own insurance
    // identity comes from JWT — never from URL
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<EmployeeInsuranceResponseDTO> getMyInsurance() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long employeeId = userDetails.getUser().getUserId();
        EmployeeInsuranceResponseDTO response =
            insurancePlanService.getEmployeeInsurance(employeeId);
        return ResponseEntity.ok(response);
    }

    // ADMIN and HR can view any employee's insurance
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<EmployeeInsuranceResponseDTO> getEmployeeInsurance(
            @PathVariable Long employeeId) {
        EmployeeInsuranceResponseDTO response =
            insurancePlanService.getEmployeeInsurance(employeeId);
        return ResponseEntity.ok(response);
    }
}