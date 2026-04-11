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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Insurance Plans", description = "Create, assign, view and deactivate insurance plans")
@RestController
@RequestMapping("/finsecure/insurance/plans")
public class InsurancePlanController {

    @Autowired
    private InsurancePlanService insurancePlanService;

    // only ADMIN can create a plan
    @Operation(summary = "Create a new insurance plan", description = "ADMIN only. Creates a base insurance plan with coverage amount.")
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
    @Operation(summary = "Get all active plans", description = "ADMIN and HR can view all currently active insurance plans.")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    @GetMapping
    public ResponseEntity<List<InsurancePlanResponseDTO>> getAllPlans() {
        List<InsurancePlanResponseDTO> plans =
            insurancePlanService.getAllInsurancePlans();
        return ResponseEntity.ok(plans);
    }

    // only ADMIN can deactivate a plan
    @Operation(summary = "Deactivate a plan", description = "ADMIN only. Soft deletes a plan — existing assignments are not affected.")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{planId}")
    public ResponseEntity<String> deactivatePlan(
            @PathVariable Long planId) {
        insurancePlanService.deactivateInsurancePlan(planId);
        return ResponseEntity.ok("Insurance plan deactivated successfully");
    }

    // only ADMIN can assign a plan to an employee
    @Operation(summary = "Assign insurance to employee", description = "ADMIN only. Assigns an active plan to an employee. Employee can only have one active insurance at a time.")
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
    @Operation(summary = "View my insurance", description = "EMPLOYEE only. Returns the logged-in employee's active insurance. Identity from JWT.")
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
    @Operation(summary = "View any employee's insurance", description = "ADMIN and HR only. Returns active insurance for the given employee ID.")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<EmployeeInsuranceResponseDTO> getEmployeeInsurance(
            @PathVariable Long employeeId) {
        EmployeeInsuranceResponseDTO response =
            insurancePlanService.getEmployeeInsurance(employeeId);
        return ResponseEntity.ok(response);
    }
}