package com.ds.app.controller;

import com.ds.app.dto.request.ClaimRequestDTO;
import com.ds.app.dto.request.ClaimStatusUpdateDTO;
import com.ds.app.dto.response.ClaimResponseDTO;
import com.ds.app.entity.ClaimStatus;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.service.InsuranceClaimService;
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

@Tag(name = "Insurance Claims", description = "Raise, view and approve/reject insurance claims")
@RestController
@RequestMapping("/finsecure/insurance/claims")
public class InsuranceClaimController {

    @Autowired
    private InsuranceClaimService insuranceClaimService;

    // only EMPLOYEE can raise a claim
    @Operation(summary = "Raise a claim", description = "EMPLOYEE only. Raises a claim against their active insurance. Claim amount cannot exceed remaining coverage.")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<ClaimResponseDTO> raiseClaim(
            @Valid @RequestBody ClaimRequestDTO dto) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long employeeId = userDetails.getUser().getUserId();
        ClaimResponseDTO response = insuranceClaimService.raiseClaim(dto, employeeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // only EMPLOYEE can view their own claims
    @Operation(summary = "View my claims", description = "EMPLOYEE only. Returns full claim history of the logged-in employee.")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @GetMapping("/my")
    public ResponseEntity<List<ClaimResponseDTO>> getMyClaims() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long employeeId = userDetails.getUser().getUserId();
        List<ClaimResponseDTO> claims = insuranceClaimService.getEmployeeClaims(employeeId);
        return ResponseEntity.ok(claims);
    }

    // ADMIN and HR can view all claims with optional status filter
    @Operation(summary = "View all claims", description = "ADMIN and HR. Filter by status. Supports pagination via page and size parameters.")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    @GetMapping
    public ResponseEntity<List<ClaimResponseDTO>> getAllClaims(
            @RequestParam(required = false) ClaimStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ClaimResponseDTO> claims = insuranceClaimService.getAllClaims(status, page, size);
        return ResponseEntity.ok(claims);
    }

    // ADMIN and HR can view a specific employee's claims
    @Operation(summary = "View claims by employee", description = "ADMIN and HR. Returns claim history for a specific employee.")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ClaimResponseDTO>> getEmployeeClaims(
            @PathVariable Long employeeId) {
        List<ClaimResponseDTO> claims = insuranceClaimService.getEmployeeClaims(employeeId);
        return ResponseEntity.ok(claims);
    }

    // only ADMIN can approve or reject a claim
    @Operation(summary = "Approve or reject a claim", description = "ADMIN only. Updates claim status. On approval, remaining coverage is deducted. resolvedBy is auto-set from JWT.")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/status")
    public ResponseEntity<ClaimResponseDTO> updateClaimStatus(
            @Valid @RequestBody ClaimStatusUpdateDTO dto) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        // get the logged in admin's username from JWT and set it on the dto
        String resolvedBy = userDetails.getUser().getUsername();

        ClaimResponseDTO response = insuranceClaimService.updateClaimStatus(dto, resolvedBy);
        return ResponseEntity.ok(response);
    }
}