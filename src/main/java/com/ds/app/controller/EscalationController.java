package com.ds.app.controller;

import com.ds.app.dto.request.EscalationRequestDTO;
import com.ds.app.dto.response.EscalationResponseDTO;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.service.EscalationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/finsecure/hr")
public class EscalationController {

    @Autowired private EscalationServiceImpl escalationService;

    private Long getLoggedInUserId() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userDetails.getUser().getUserId();
    }

    private String getLoggedInRole() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userDetails.getUser().getRole().toString();
    }

    // Manager, HR, Admin can raise — Employee is blocked inside service
    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN') or hasAuthority('MANAGER') ")
    @PostMapping("/escalation")
    public ResponseEntity<EscalationResponseDTO> raise(@Valid @RequestBody EscalationRequestDTO req) {
        return new ResponseEntity<>(
                escalationService.raise(req, getLoggedInUserId(), getLoggedInRole()),
                HttpStatus.CREATED);
    }

    // HR, Admin — full list
    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/escalation")
    public ResponseEntity<Map<String, Object>> getAllEscalations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(escalationService.getAll(page, size));
    }

    
    
    // All roles — service filters response based on role
    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
    @GetMapping("/escalation/employee/{userId}")
    public ResponseEntity<Map<String, Object>> getForEmployee(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                escalationService.getForEmployee(userId, getLoggedInRole(), page, size));
    }
    
    
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @GetMapping("/my-escalations")
    public ResponseEntity<Map<String, Object>> getMyEscalations(
           
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
       Long currentUserId=getLoggedInUserId();
        
        return ResponseEntity.ok(escalationService.getForEmployee(currentUserId, getLoggedInRole(), page, size));
    }


    
    
    // HR, Admin — resolve / update status
    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @PutMapping("/escalation/{id}/status")
    public ResponseEntity<EscalationResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(escalationService.updateStatus(id, status));
    }
}

