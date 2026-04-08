package com.ds.app.controller;

import com.ds.app.dto.request.ProjectRequestDTO;
import com.ds.app.dto.response.ProjectResponseDTO;
import com.ds.app.entity.Project;
import com.ds.app.service.ProjectServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finsecure/hr")
public class ProjectController {

    @Autowired private ProjectServiceImpl projectService;

    @PreAuthorize("hasAuthority('HR')")
    @PostMapping("/project")
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO req) {
        return new ResponseEntity<>(projectService.create(req), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/project")
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAll());
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/project/{id}")
    public ResponseEntity<ProjectResponseDTO> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @GetMapping("/project/company/{companyId}")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(projectService.getByCompany(companyId));
    }

    @PreAuthorize("hasAuthority('HR')")
    @PutMapping("/project/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Long id,
                                                  @Valid @RequestBody ProjectRequestDTO req) {
        return ResponseEntity.ok(projectService.update(id, req));
    }
    
 
    @PreAuthorize("hasAuthority('HR') or hasAuthority('ADMIN')")
    @PatchMapping("/project/{id}/status")
    public ResponseEntity<ProjectResponseDTO> updateProjectStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(projectService.updateStatus(id, status));
    }

}
