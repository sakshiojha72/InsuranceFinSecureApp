package com.ds.app.controller;

import com.ds.app.dto.request.BuyTopUpRequestDTO;
import com.ds.app.dto.request.CreateTopUpPlanRequestDTO;
import com.ds.app.dto.response.EmployeeTopUpResponseDTO;
import com.ds.app.dto.response.TopUpPlanResponseDTO;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.service.TopUpService;
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

@Tag(name = "Top-Up Plans", description = "Create and purchase additional coverage top-ups")
@RestController
@RequestMapping("/finsecure/insurance/topups")
public class TopUpController {

    @Autowired
    private TopUpService topUpService;

    // only ADMIN can create a top-up plan
    @Operation(summary = "Create a top-up plan", description = "ADMIN only. Creates a new top-up plan with additional coverage and price.")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/plans")
    public ResponseEntity<TopUpPlanResponseDTO> createTopUpPlan(
            @Valid @RequestBody CreateTopUpPlanRequestDTO dto) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUser().getUsername();
        TopUpPlanResponseDTO response = topUpService.createTopUpPlan(dto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ADMIN, HR and EMPLOYEE can browse available top-up plans
    @Operation(summary = "View all active top-up plans", description = "ADMIN, HR and EMPLOYEE can browse available top-up plans.")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR') or hasAuthority('EMPLOYEE')")
    @GetMapping("/plans")
    public ResponseEntity<List<TopUpPlanResponseDTO>> getAllTopUpPlans() {
        List<TopUpPlanResponseDTO> plans = topUpService.getAllTopUpPlans();
        return ResponseEntity.ok(plans);
    }

    // only ADMIN can deactivate a top-up plan
    @Operation(summary = "Deactivate a top-up plan", description = "ADMIN only. Soft deletes a top-up plan.")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/plans/{topUpPlanId}")
    public ResponseEntity<String> deactivateTopUpPlan(
            @PathVariable Long topUpPlanId) {
        topUpService.deactivateTopUpPlan(topUpPlanId);
        return ResponseEntity.ok("Top-up plan deactivated successfully");
    }

    // only EMPLOYEE can buy a top-up
    @Operation(summary = "Buy a top-up", description = "EMPLOYEE only. Purchases a top-up plan. Employee must have active base insurance. remainingCoverage is increased immediately.")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @PostMapping("/buy")
    public ResponseEntity<EmployeeTopUpResponseDTO> buyTopUp(
            @Valid @RequestBody BuyTopUpRequestDTO dto) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long employeeId = userDetails.getUser().getUserId();
        EmployeeTopUpResponseDTO response = topUpService.buyTopUp(dto, employeeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // only EMPLOYEE can view their own top-ups
    @Operation(summary = "View my top-ups", description = "EMPLOYEE only. Returns all top-ups purchased by the logged-in employee.")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<List<EmployeeTopUpResponseDTO>> getMyTopUps() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long employeeId = userDetails.getUser().getUserId();
        List<EmployeeTopUpResponseDTO> topUps = topUpService.getEmployeeTopUps(employeeId);
        return ResponseEntity.ok(topUps);
    }

    // ADMIN and HR can view any employee's top-ups
    @Operation(summary = "View employee top-ups", description = "ADMIN and HR. Returns all top-ups purchased by a specific employee.")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeTopUpResponseDTO>> getEmployeeTopUps(
            @PathVariable Long employeeId) {
        List<EmployeeTopUpResponseDTO> topUps = topUpService.getEmployeeTopUps(employeeId);
        return ResponseEntity.ok(topUps);
    }
}