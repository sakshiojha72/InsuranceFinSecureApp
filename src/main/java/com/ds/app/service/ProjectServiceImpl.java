package com.ds.app.service;

import com.ds.app.dto.request.ProjectRequestDTO;
import com.ds.app.dto.response.ProjectResponseDTO;
import com.ds.app.entity.Project;
import com.ds.app.exception.HrBusinessRuleException;
import com.ds.app.exception.HrException;
import com.ds.app.exception.HrResourceNotFoundException;
import com.ds.app.repository.iProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl {

    @Autowired private iProjectRepository projectRepo;
    @Autowired private CompanyServiceImpl companyService;
    @Autowired private DepartmentServiceImpl departmentService;

    // ── entity → response DTO
    public ProjectResponseDTO toResponse(Project p) {
        ProjectResponseDTO res = new ProjectResponseDTO();
        res.setId(p.getId());
        res.setName(p.getName());
        res.setStatus(p.getStatus());
        res.setStartDate(p.getStartDate());
        res.setEndDate(p.getEndDate());
        res.setCompanyId(p.getCompanyId());
        res.setCompanyName(p.getCompany() != null ? p.getCompany().getName() : null);
        res.setDepartmentId(p.getDepartmentId());
        res.setDepartmentName(p.getDepartment() != null ? p.getDepartment().getName() : null);
        return res;
    }

    // ── create
    public ProjectResponseDTO create(ProjectRequestDTO req) {
        Project project = new Project();
        project.setName(req.getName());
        project.setCompany(companyService.findOrThrow(req.getCompanyId()));
        project.setDepartment(departmentService.findOrThrow(req.getDepartmentId()));
        project.setStatus(req.getStatus() != null ? req.getStatus() : "ACTIVE");
        project.setStartDate(req.getStartDate());
        project.setEndDate(req.getEndDate());
        return toResponse(projectRepo.save(project));
    }

    // ── update 
    public ProjectResponseDTO update(Long id, ProjectRequestDTO req) {
        Project project = findOrThrow(id);
        if (req.getName()         != null) project.setName(req.getName());
        if (req.getStatus()       != null) project.setStatus(req.getStatus());
        if (req.getStartDate()    != null) project.setStartDate(req.getStartDate());
        if (req.getEndDate()      != null) project.setEndDate(req.getEndDate());
        if (req.getCompanyId()    != null) project.setCompany(companyService.findOrThrow(req.getCompanyId()));
        if (req.getDepartmentId() != null) project.setDepartment(departmentService.findOrThrow(req.getDepartmentId()));
        return toResponse(projectRepo.save(project));
    }

    // ── get all 
    public List<ProjectResponseDTO> getAll() {
        return projectRepo.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ── get by id 
    public ProjectResponseDTO getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    // ── get by company 
    public List<ProjectResponseDTO> getByCompany(Long companyId) {
        return projectRepo.findByCompany(companyService.findOrThrow(companyId))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }
    

    // update status
    public ProjectResponseDTO updateStatus(Long id, String status) {
        if (!status.equals("ACTIVE") && !status.equals("INACTIVE") && !status.equals("COMPLETED") && !status.equals("ON_HOLD"))
            throw new HrBusinessRuleException("Status must be ACTIVE, INACTIVE, COMPLETED or ON_HOLD");
        Project project = findOrThrow(id);
        project.setStatus(status);
        return toResponse(projectRepo.save(project));
    }



    // -- internal helper 
    public Project findOrThrow(Long id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new HrResourceNotFoundException("Project ",id));
    }
}
