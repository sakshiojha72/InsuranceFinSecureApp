



package com.ds.app.service;

import com.ds.app.dto.request.DepartmentRequestDTO;
import com.ds.app.dto.response.DepartmentResponseDTO;
import com.ds.app.entity.Company;
import com.ds.app.entity.Department;
import com.ds.app.exception.HrDuplicateResourceException;
import com.ds.app.exception.HrException;
import com.ds.app.exception.HrResourceNotFoundException;
import com.ds.app.repository.iDepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl {

    @Autowired private iDepartmentRepository departmentRepo;
    @Autowired private CompanyServiceImpl companyService;

    //  entity → response DTO 
    public DepartmentResponseDTO toResponse(Department d) {
        DepartmentResponseDTO res = new DepartmentResponseDTO();
        res.setId(d.getId());
        res.setName(d.getName());
        res.setCode(d.getCode());
        res.setStatus(d.getStatus());
        res.setCompanyId(d.getCompanyId());
        res.setCompanyName(d.getCompany() != null ? d.getCompany().getName() : null);
        return res;
    }

    //create
    public DepartmentResponseDTO create(DepartmentRequestDTO req) {
        Company company = companyService.findOrThrow(req.getCompanyId());
        if (departmentRepo.existsByCodeAndCompany(req.getCode(), company))
            throw new HrDuplicateResourceException("Department " ,"Code", req.getCode());

        Department dept = new Department();
        dept.setName(req.getName());
        dept.setCode(req.getCode());
        dept.setCompany(company);
        dept.setStatus(req.getStatus() != null ? req.getStatus() : "ACTIVE");
        return toResponse(departmentRepo.save(dept));
    }

    // update
    public DepartmentResponseDTO update(Long id, DepartmentRequestDTO req) {
        Department dept = findOrThrow(id);
        if (req.getName()      != null) dept.setName(req.getName());
        if (req.getCode()      != null) dept.setCode(req.getCode());
        if (req.getStatus()    != null) dept.setStatus(req.getStatus());
        if (req.getCompanyId() != null) dept.setCompany(companyService.findOrThrow(req.getCompanyId()));
        return toResponse(departmentRepo.save(dept));
    }

    //--- get all---------------------
    public List<DepartmentResponseDTO> getAll() {
        return departmentRepo.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    //--- get by id--------------------
    public DepartmentResponseDTO getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    //--- get by company-----------------
    public List<DepartmentResponseDTO> getByCompany(Long companyId) {
        return departmentRepo.findByCompany(companyService.findOrThrow(companyId))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }
    
    
    //update status (ACTIVE/INACTIVE)
    public DepartmentResponseDTO updateStatus(Long id, String status) {
        if (!status.equals("ACTIVE") && !status.equals("INACTIVE"))
            throw new HrException("Status must be ACTIVE or INACTIVE");
        Department dept = findOrThrow(id);
        dept.setStatus(status);
        return toResponse(departmentRepo.save(dept));
    }

   



    //--- internal helper-----------------------
    public Department findOrThrow(Long id) {
        return departmentRepo.findById(id)
                .orElseThrow(() -> new HrResourceNotFoundException("Department " , id));
    }
}

