package com.ds.app.controller;

import com.ds.app.dto.request.AppraisalRequestDTO;
import com.ds.app.dto.response.AppraisalResponseDTO;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.service.AppraisalServiceImpl;
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
public class AppraisalController {

    @Autowired private AppraisalServiceImpl appraisalService;

    private Long getLoggedInUserId() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userDetails.getUser().getUserId();
    }

    // HR only — no one else can initiate an appraisal
    @PreAuthorize("hasAuthority('HR')")
    @PostMapping("/appraisal")
    public ResponseEntity<AppraisalResponseDTO> initiate(@Valid @RequestBody AppraisalRequestDTO req) {
        return new ResponseEntity<>(
                appraisalService.initiate(req, getLoggedInUserId()),
                HttpStatus.CREATED);
    }

    // HR + ADMIN can view full appraisal history
    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/appraisal/employee/{userId}")
    public ResponseEntity<Map<String, Object>> getHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(appraisalService.getHistory(userId, page, size));
    }
}

