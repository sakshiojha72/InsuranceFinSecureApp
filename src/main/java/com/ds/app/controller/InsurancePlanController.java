package com.ds.app.controller;

import com.ds.app.dto.request.AssignInsuranceRequestDTO;
import com.ds.app.dto.request.CreateInsurancePlanRequestDTO;
import com.ds.app.dto.response.EmployeeInsuranceResponseDTO;
import com.ds.app.dto.response.InsurancePlanResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.InsurancePlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/finsecure/insurance/plans")
public class InsurancePlanController {

    @Autowired
    private InsurancePlanService insurancePlanService;

    @Autowired
    private EmployeeRepository employeeRepository;

    // only ADMIN can create a plan
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<InsurancePlanResponseDTO> createInsurancePlan(
            @Valid @RequestBody CreateInsurancePlanRequestDTO dto, Principal principal) {

        InsurancePlanResponseDTO response =
            insurancePlanService.createInsurancePlan(dto, principal.getName());
        //p.getname returns username from jwt

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ADMIN and HR can view all plans
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<List<InsurancePlanResponseDTO>> getAllPlans() {

        List<InsurancePlanResponseDTO> plans =
            insurancePlanService.getAllInsurancePlans();

        return ResponseEntity.ok(plans);
    }

    // only ADMIN can deactivate a plan
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{planId}")
    public ResponseEntity<String> deactivatePlan(
            @PathVariable Long planId) {

        insurancePlanService.deactivateInsurancePlan(planId);

        return ResponseEntity.ok("Insurance plan deactivated successfully");
    }

    // only ADMIN can assign a plan to an employee
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign")
    public ResponseEntity<EmployeeInsuranceResponseDTO> assignInsurance(
            @Valid @RequestBody AssignInsuranceRequestDTO dto) {

        EmployeeInsuranceResponseDTO response =
            insurancePlanService.assignInsurance(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // only EMPLOYEE can view their own insurance
    // identity comes from JWT — never from URL
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<EmployeeInsuranceResponseDTO> getMyInsurance(
            Principal principal) {

        Employee employee = employeeRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeInsuranceResponseDTO response =
            insurancePlanService.getEmployeeInsurance(employee.getUserId());

        return ResponseEntity.ok(response);
    }

    // ADMIN and HR can view any employee's insurance
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<EmployeeInsuranceResponseDTO> getEmployeeInsurance(
            @PathVariable Long employeeId) {

        EmployeeInsuranceResponseDTO response =
            insurancePlanService.getEmployeeInsurance(employeeId);

        return ResponseEntity.ok(response);
    }
}