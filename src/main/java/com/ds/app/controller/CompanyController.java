package com.ds.app.controller;

import com.ds.app.dto.request.CompanyRequestDTO;
import com.ds.app.dto.request.DepartmentRequestDTO;
import com.ds.app.dto.response.CompanyResponseDTO;
import com.ds.app.entity.Company;
import com.ds.app.entity.Department;
import com.ds.app.service.CompanyServiceImpl;
import com.ds.app.service.DepartmentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finsecure/hr")
public class CompanyController {

    @Autowired private CompanyServiceImpl companyService;
    @Autowired private DepartmentServiceImpl departmentService;

    

    @PreAuthorize("hasAuthority('HR')")
    @PostMapping("/company")
    public ResponseEntity<CompanyResponseDTO> createCompany(@Valid @RequestBody CompanyRequestDTO company) {
        return new ResponseEntity<>(companyService.create(company), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/company")
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAll());
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/company/{id}")
    public ResponseEntity<CompanyResponseDTO> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getById(id));
    }

    @PreAuthorize("hasAuthority('HR')")
    @PutMapping("/company/{id}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(@PathVariable Long id,
                                                  @RequestBody CompanyRequestDTO updated) {
        return ResponseEntity.ok(companyService.update(id, updated));
    }
    
    
    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @PatchMapping("/company/{id}/status")
    public ResponseEntity<CompanyResponseDTO> updateCompanyStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(companyService.updateStatus(id, status));
    }


   
}

