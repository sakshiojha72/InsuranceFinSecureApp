
package com.ds.app.service;

import com.ds.app.dto.request.CompanyRequestDTO;
import com.ds.app.dto.response.CompanyResponseDTO;
import com.ds.app.entity.Company;
import com.ds.app.exception.HrDuplicateResourceException;
import com.ds.app.exception.HrException;
import com.ds.app.exception.HrResourceNotFoundException;
import com.ds.app.repository.iCompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl {

    @Autowired private iCompanyRepository companyRepo;

    //  entity → response DTO---------
    public CompanyResponseDTO toResponse(Company c) {
        CompanyResponseDTO res = new CompanyResponseDTO();
        res.setId(c.getId());
        res.setName(c.getName());
        res.setCode(c.getCode());
        res.setRestrictsInvestment(c.getRestrictsInvestment());
        res.setStatus(c.getStatus());
        return res;
    }

    // -- create-------------------
    public CompanyResponseDTO create(CompanyRequestDTO req) {
        if (companyRepo.existsByCode(req.getCode()))
            throw new HrDuplicateResourceException("Company","Code",req.getCode());

        Company company = new Company();
        company.setName(req.getName());
        company.setCode(req.getCode());
        company.setRestrictsInvestment(req.getRestrictsInvestment() != null
                ? req.getRestrictsInvestment() : false);
        company.setStatus(req.getStatus() != null ? req.getStatus() : "ACTIVE");

        return toResponse(companyRepo.save(company));
    }

    //update-----------------
    public CompanyResponseDTO update(Long id, CompanyRequestDTO req) {
        Company company = findOrThrow(id);
        if (req.getName()               != null) company.setName(req.getName());
        if (req.getCode()               != null) company.setCode(req.getCode());
        if (req.getRestrictsInvestment() != null) company.setRestrictsInvestment(req.getRestrictsInvestment());
        if (req.getStatus()             != null) company.setStatus(req.getStatus());
        return toResponse(companyRepo.save(company));
    }

    // ---- get all ---------------------
    public List<CompanyResponseDTO> getAll() {
        return companyRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ---- get by id -----------------------
    public CompanyResponseDTO getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    
    //update status-------------------------------
    public CompanyResponseDTO updateStatus(Long id, String status) {
        if (!status.equals("ACTIVE") && !status.equals("INACTIVE"))
            throw new HrException("Status must be ACTIVE or INACTIVE");
        Company company = findOrThrow(id);
        company.setStatus(status);
        return toResponse(companyRepo.save(company));
    }

    // ---- internal helper — returns raw entity for other services ------------─
    public Company findOrThrow(Long id) {
        return companyRepo.findById(id)
                .orElseThrow(() -> new HrResourceNotFoundException("Company " , id));
    }
}

