package com.ds.app.service;

import com.ds.app.dto.request.EscalationRequestDTO;
import com.ds.app.dto.response.EscalationResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.Escalation;
import com.ds.app.enums.EscalationStatus;
import com.ds.app.exception.HrBusinessRuleException;
import com.ds.app.exception.HrException;
import com.ds.app.exception.HrResourceNotFoundException;
import com.ds.app.repository.iEscalationRepository;
import com.ds.app.repository.iEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EscalationServiceImpl {

    @Autowired private iEscalationRepository escalationRepo;
    @Autowired private iEmployeeRepository employeeRepo;
    @Autowired private EmployeeServiceImpl employeeService;
    @Autowired private EmailServiceImpl emailService;
   

    public EscalationResponseDTO raise(EscalationRequestDTO req,
                                       Long raisedByUserId, String role) {

        Employee raiser = employeeService.findOrThrow(raisedByUserId);
        Employee target = employeeService.findOrThrow(req.getTargetEmployeeUserId());

        // Employee role cannot raise escalations
        if ("EMPLOYEE".equals(role))
            throw new HrBusinessRuleException("Employees cannot raise escalations");

        // Manager — same company AND same department only
        if ("MANAGER".equals(role) ) {
            if (raiser.getCompanyId() == null ||
                !raiser.getCompanyId().equals(target.getCompanyId()))
                throw new HrBusinessRuleException("Managers can only escalate within their own company");

            if (raiser.getDepartmentId() == null ||
                !raiser.getDepartmentId().equals(target.getDepartmentId()))
                throw new HrBusinessRuleException("Managers can only escalate within their own department");
        }

        Escalation esc = new Escalation();
        esc.setRaisedBy(raiser);
        esc.setTargetEmployee(target);
        esc.setDepartmentId(target.getDepartmentId());  // snapshot
        esc.setCompanyId(target.getCompanyId());         // snapshot
        esc.setComment(req.getComment());
        esc.setStatus(EscalationStatus.OPEN);
        esc.setRaisedAt(LocalDateTime.now());
        escalationRepo.save(esc);

        // flag the target employee
        target.setIsEscalated(true);
        employeeRepo.save(target);

     // notify target via email
        emailService.sendEscalationEmail(target.getEmail(), target.getFirstName());


        return toResponse(esc);
    }

    // HR / Admin / Manager — full escalation list, paginated
    public Map<String, Object> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("raisedAt").descending());
        Page<Escalation> result = escalationRepo.findAll(pageable);
        return buildPage(result);
    }

    // get escalations for one employee — role decides what is returned
    public Map<String, Object> getForEmployee(Long targetUserId, String role, int page, int size) {
        Employee target = employeeService.findOrThrow(targetUserId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("raisedAt").descending());
        Page<Escalation> result = escalationRepo.findByTargetEmployee(target, pageable);

        // Employee sees only comment + status (no raiser details)
        if ("EMPLOYEE".equals(role)) {
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("isEscalated", target.getIsEscalated());
            res.put("escalations", result.getContent().stream()
                    .map(e -> Map.of(
                            "comment",  e.getComment(),
                            "status",   e.getStatus(),
                            "raisedAt", e.getRaisedAt()
                    )).collect(Collectors.toList()));
            res.put("totalItems", result.getTotalElements());
            res.put("totalPages", result.getTotalPages());
            return res;
        }

        return buildPage(result);
    }

    // update status: OPEN → IN_PROGRESS → RESOLVED
    public EscalationResponseDTO updateStatus(Long escalationId, String newStatus) {
        Escalation esc = escalationRepo.findById(escalationId)
                .orElseThrow(() -> new HrResourceNotFoundException("Escalation" , escalationId));

        EscalationStatus status;
        try {
            status = EscalationStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new HrException("Invalid status. Use OPEN / IN_PROGRESS / RESOLVED");
        }

        esc.setStatus(status);

        if (status == EscalationStatus.RESOLVED) {
            esc.setResolvedAt(LocalDateTime.now());

            // clear isEscalated flag only if no other open escalations remain
            List<Escalation> stillOpen = escalationRepo
                    .findByTargetEmployeeAndStatus(esc.getTargetEmployee(), EscalationStatus.OPEN);
            if (stillOpen.isEmpty()) {
                Employee target = esc.getTargetEmployee();
                target.setIsEscalated(false);
                employeeRepo.save(target);
            }
        }

        return toResponse(escalationRepo.save(esc));
    }

    // entity ->response dto
    private EscalationResponseDTO toResponse(Escalation esc) {
        EscalationResponseDTO res = new EscalationResponseDTO();
        res.setId(esc.getId());
        res.setRaisedByUserId(esc.getRaisedBy().getUserId());
        res.setRaisedByName(esc.getRaisedBy().getFirstName());
        res.setTargetEmployeeUserId(esc.getTargetEmployee().getUserId());
        res.setTargetEmployeeName(esc.getTargetEmployee().getFirstName());
        res.setDepartmentId(esc.getDepartmentId());
        res.setCompanyId(esc.getCompanyId());
        res.setComment(esc.getComment());
        res.setStatus(esc.getStatus());
        res.setRaisedAt(esc.getRaisedAt());
        res.setResolvedAt(esc.getResolvedAt());
        return res;
    }

    
    
    
    private Map<String, Object> buildPage(Page<Escalation> page) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("content",     page.getContent().stream().map(this::toResponse).collect(Collectors.toList()));
        res.put("currentPage", page.getNumber());
        res.put("totalItems",  page.getTotalElements());
        res.put("totalPages",  page.getTotalPages());
        res.put("isLast",      page.isLast());
        return res;
    }
}
