package com.ds.app.service;

import com.ds.app.dto.request.AppraisalRequestDTO;
import com.ds.app.dto.response.AppraisalResponseDTO;
import com.ds.app.entity.Appraisal;
import com.ds.app.entity.Employee;
import com.ds.app.exception.HrException;
import com.ds.app.repository.iAppraisalRepository;
import com.ds.app.repository.iEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppraisalServiceImpl {

    @Autowired private iAppraisalRepository appraisalRepo;
    @Autowired private iEmployeeRepository employeeRepo;
    @Autowired private EmployeeServiceImpl employeeService;
    @Autowired private EmailServiceImpl emailService;

    // both saves in one transaction — if one fails, both roll back
    @Transactional
    public AppraisalResponseDTO initiate(AppraisalRequestDTO req, Long hrUserId) {

        Employee emp = employeeService.findOrThrow(req.getEmployeeUserId());
        Employee hr  = employeeService.findOrThrow(hrUserId);

        // Step 1 — insert permanent appraisal record
        Appraisal appraisal = new Appraisal();
        appraisal.setEmployee(emp);
        appraisal.setInitiatedByHr(hr);
        appraisal.setPreviousSalary(emp.getCurrentSalary());       // snapshot before update
        appraisal.setRevisedSalary(req.getRevisedSalary());
        appraisal.setRemarks(req.getRemarks());
        appraisal.setAppraisalYear(req.getAppraisalYear());
        appraisal.setAppraisalDate(LocalDate.now());
        appraisalRepo.save(appraisal);

        // Step 2 — update live salary on employee
        emp.setCurrentSalary(req.getRevisedSalary());
        employeeRepo.save(emp);

        // Step 3 — notify employee
        
        emailService.sendAppraisalEmail(
                emp.getEmail(), emp.getUsername(), req.getRevisedSalary());

        return toResponse(appraisal);
    }

    // HR — full history paginated
    public Map<String, Object> getHistory(Long employeeUserId, int page, int size) {
        Employee emp = employeeService.findOrThrow(employeeUserId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("appraisalDate").descending());
        Page<Appraisal> result = appraisalRepo.findByEmployee(emp, pageable);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("content",     result.getContent().stream().map(this::toResponse).collect(Collectors.toList()));
        res.put("currentPage", result.getNumber());
        res.put("totalItems",  result.getTotalElements());
        res.put("totalPages",  result.getTotalPages());
        res.put("isLast",      result.isLast());
        return res;
    }

    //  entity → response DTO 
    private AppraisalResponseDTO toResponse(Appraisal a) {
        AppraisalResponseDTO res = new AppraisalResponseDTO();
        res.setId(a.getId());
        res.setEmployeeUserId(a.getEmployee().getUserId());
        res.setInitiatedByHrUserId(a.getInitiatedByHr().getUserId());
        res.setPreviousSalary(a.getPreviousSalary());
        res.setRevisedSalary(a.getRevisedSalary());
        res.setRemarks(a.getRemarks());
        res.setAppraisalYear(a.getAppraisalYear());
        res.setAppraisalDate(a.getAppraisalDate());
        return res;
    }
}
