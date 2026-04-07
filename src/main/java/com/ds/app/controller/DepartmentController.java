package com.ds.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.request.DepartmentRequestDTO;
import com.ds.app.dto.response.DepartmentResponseDTO;
import com.ds.app.entity.Department;
import com.ds.app.service.CompanyServiceImpl;
import com.ds.app.service.DepartmentServiceImpl;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/finsecure/hr")
public class DepartmentController {
	
	 // DEPARTMENT 
	
	  @Autowired private CompanyServiceImpl companyService;
	  @Autowired private DepartmentServiceImpl departmentService;

	    

    @PreAuthorize("hasAuthority('HR')")
    @PostMapping("/department")
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@Valid @RequestBody DepartmentRequestDTO req) {
        return new ResponseEntity<>(departmentService.create(req), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/department")
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAll());
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/department/{id}")
    public ResponseEntity<DepartmentResponseDTO> getDepartment(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getById(id));
    }
    
 
    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @PatchMapping("/department/{id}/status")
    public ResponseEntity<DepartmentResponseDTO> updateDeptStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(departmentService.updateStatus(id, status));
    }


    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/department/company/{companyId}")
    public ResponseEntity<List<DepartmentResponseDTO>> getDeptsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(departmentService.getByCompany(companyId));
    }

    @PreAuthorize("hasAuthority('HR')")
    @PutMapping("/department/{id}")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(@PathVariable Long id,
                                                        @Valid @RequestBody DepartmentRequestDTO req) {
        return ResponseEntity.ok(departmentService.update(id, req));
    }

}
