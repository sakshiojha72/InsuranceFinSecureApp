package com.ds.app.controller;

import com.ds.app.dto.request.BuyTopUpRequestDTO;
import com.ds.app.dto.request.CreateTopUpPlanRequestDTO;
import com.ds.app.dto.response.EmployeeTopUpResponseDTO;
import com.ds.app.dto.response.TopUpPlanResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.TopUpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/finsecure/insurance/topups")
public class TopUpController {

    @Autowired
    private TopUpService topUpService;

    @Autowired
    private EmployeeRepository employeeRepository;

    // only ADMIN can create a top-up plan
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/plans")
    public ResponseEntity<TopUpPlanResponseDTO> createTopUpPlan(
            @Valid @RequestBody CreateTopUpPlanRequestDTO dto, Principal principal) {

        TopUpPlanResponseDTO response =
            topUpService.createTopUpPlan(dto, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ADMIN and EMPLOYEE can browse available top-up plans
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    @GetMapping("/plans")
    public ResponseEntity<List<TopUpPlanResponseDTO>> getAllTopUpPlans() {

        List<TopUpPlanResponseDTO> plans =
            topUpService.getAllTopUpPlans();

        return ResponseEntity.ok(plans);
    }

    // only ADMIN can deactivate a top-up plan
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/plans/{topUpPlanId}")
    public ResponseEntity<String> deactivateTopUpPlan(
            @PathVariable Long topUpPlanId) {

        topUpService.deactivateTopUpPlan(topUpPlanId);

        return ResponseEntity.ok("Top-up plan deactivated successfully");
    }

    // only EMPLOYEE can buy a top-up
    // employeeId from JWT — never from body
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/buy")
    public ResponseEntity<EmployeeTopUpResponseDTO> buyTopUp(
            @Valid @RequestBody BuyTopUpRequestDTO dto,
            Principal principal) {

        Employee employee = employeeRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeTopUpResponseDTO response =
            topUpService.buyTopUp(dto, employee.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // only EMPLOYEE can view their own top-ups
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<List<EmployeeTopUpResponseDTO>> getMyTopUps(
            Principal principal) {

        Employee employee = employeeRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<EmployeeTopUpResponseDTO> topUps =
            topUpService.getEmployeeTopUps(employee.getUserId());

        return ResponseEntity.ok(topUps);
    }

    // ADMIN and HR can view any employee's top-ups
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeTopUpResponseDTO>> getEmployeeTopUps(
            @PathVariable Long employeeId) {

        List<EmployeeTopUpResponseDTO> topUps =
            topUpService.getEmployeeTopUps(employeeId);

        return ResponseEntity.ok(topUps);
    }
}