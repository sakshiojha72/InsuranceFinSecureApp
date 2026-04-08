package com.ds.app.controller;

import com.ds.app.dto.request.AllocationRequestDTO;
import com.ds.app.dto.request.DeallocationRequestDTO;
import com.ds.app.entity.AllocationHistory;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.service.AllocationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finsecure/hr")
public class AllocationController {

    @Autowired private AllocationServiceImpl allocationService;

    // get the logged-in user's ID from JWT via SecurityContext
    private Long getLoggedInUserId() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userDetails.getUser().getUserId();
    }

    
    @PreAuthorize("hasAuthority('HR')")
    @PostMapping("/allocate")
    public ResponseEntity<String> assign(@Valid @RequestBody AllocationRequestDTO req) {
        return ResponseEntity.ok(allocationService.assign(req, getLoggedInUserId()));
    }

    
    @PreAuthorize("hasAuthority('HR')")
    @PostMapping("/deallocate")
    public ResponseEntity<String> deallocate(@Valid @RequestBody DeallocationRequestDTO req) {
        return ResponseEntity.ok(allocationService.deallocate(req, getLoggedInUserId()));
    }

    // ADMIN + HR can view history
    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/history/{employeeUserId}")
    public ResponseEntity<List<AllocationHistory>> getHistory(@PathVariable Long employeeUserId) {
        return ResponseEntity.ok(allocationService.getHistory(employeeUserId));
    }
}
