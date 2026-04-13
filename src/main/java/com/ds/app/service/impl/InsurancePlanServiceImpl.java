package com.ds.app.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ds.app.dto.request.AssignInsuranceRequestDTO;
import com.ds.app.dto.request.CreateInsurancePlanRequestDTO;
import com.ds.app.dto.response.EmployeeInsuranceResponseDTO;
import com.ds.app.dto.response.InsurancePlanResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeInsurance;
import com.ds.app.entity.InsurancePlan;
import com.ds.app.entity.InsuranceStatus;
import com.ds.app.exception.EmployeeNotFoundException;
import com.ds.app.exception.InsuranceAlreadyAssignedException;
import com.ds.app.exception.InsurancePlanNotFoundException;
import com.ds.app.repository.EmployeeInsuranceRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.InsurancePlanRepository;
import com.ds.app.service.EmailService;
import com.ds.app.service.InsurancePlanService;

@Service
public class InsurancePlanServiceImpl implements InsurancePlanService {

	@Autowired
	private EmailService emailService;
	
    @Autowired
    private InsurancePlanRepository insurancePlanRepository;

    @Autowired
    private EmployeeInsuranceRepository employeeInsuranceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // ─── CREATE PLAN ──────────────────────────────────────────────────────────
    @Override
    public InsurancePlanResponseDTO createInsurancePlan(
            CreateInsurancePlanRequestDTO dto, String createdBy) {

        if (insurancePlanRepository.existsByPlanName(dto.getPlanName())) {
            throw new RuntimeException(
                "Insurance plan with this name already exists");
        }

        InsurancePlan plan = new InsurancePlan();
        plan.setPlanName(dto.getPlanName());
        plan.setCoverageAmount(dto.getCoverageAmount());
        plan.setDescription(dto.getDescription());
        plan.setCreatedBy(createdBy); 
        plan.setIsActive(true);       

        InsurancePlan saved = insurancePlanRepository.save(plan);
        return mapToPlanResponse(saved);
    }

    // ─── GET ALL ACTIVE PLANS ─────────────────────────────────────────────────
    @Override
    public List<InsurancePlanResponseDTO> getAllInsurancePlans() {

        // only return active plans
        List<InsurancePlan> plans = insurancePlanRepository.findByIsActiveTrue();
        List<InsurancePlanResponseDTO> result = new ArrayList<>();

        for (InsurancePlan plan : plans) {
            result.add(mapToPlanResponse(plan));
        }
        return result;
    }

    // ─── DEACTIVATE PLAN ──────────────────────────────────────────────────────
    @Override
    public void deactivateInsurancePlan(Long planId) {

        InsurancePlan plan = insurancePlanRepository.findById(planId)
            .orElseThrow(() -> new InsurancePlanNotFoundException(planId));

        // plan already deactivated 
        if (!plan.getIsActive()) {
            throw new RuntimeException(
                "Insurance plan is already deactivated");
        }

        // soft delete
        plan.setIsActive(false);
        insurancePlanRepository.save(plan);
    }

    // ─── ASSIGN INSURANCE TO EMPLOYEE ─────────────────────────────────────────
    @Override
    public EmployeeInsuranceResponseDTO assignInsurance(
            AssignInsuranceRequestDTO dto, String assignedBy) {

        // 1. employee must exist
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
            .orElseThrow(() -> new EmployeeNotFoundException(dto.getEmployeeId()));

        // 2. plan must exist
        InsurancePlan plan = insurancePlanRepository.findById(dto.getPlanId())
            .orElseThrow(() -> new InsurancePlanNotFoundException(dto.getPlanId()));

        // 3. can't assign a deactivated plan
        if (!plan.getIsActive()) {
            throw new RuntimeException(
                "Cannot assign a deactivated insurance plan. "
                + "Please choose an active plan.");
        }

        // 4. employee can only have ONE active insurance at a time
        if (employeeInsuranceRepository.existsByEmployee_UserIdAndStatus(
                dto.getEmployeeId(), InsuranceStatus.ACTIVE)) {
            throw new InsuranceAlreadyAssignedException(dto.getEmployeeId());
        }

        // 5. expiry date must be in the future
        if (dto.getExpiryDate() != null &&
                !dto.getExpiryDate().isAfter(LocalDate.now())) {
            throw new RuntimeException(
                "Expiry date must be a future date");
        }
        EmployeeInsurance insurance = new EmployeeInsurance();
        insurance.setEmployee(employee);
        insurance.setInsurancePlan(plan);
        insurance.setAssignedDate(LocalDate.now());
        insurance.setExpiryDate(dto.getExpiryDate());
        insurance.setStatus(InsuranceStatus.ACTIVE);
        insurance.setRemainingCoverage(plan.getCoverageAmount());
        insurance.setAssignedBy(assignedBy);
        EmployeeInsurance saved = employeeInsuranceRepository.save(insurance);

     // notify employee their insurance is active
         String employeeEmail = employee.getEmail();
         emailService.sendInsuranceAssignedEmail(
         employeeEmail,
         employee.getFirstName() + " " + employee.getLastName(),
         plan.getPlanName(),
         plan.getCoverageAmount(),
         dto.getExpiryDate().toString());

     return mapToInsuranceResponse(saved);
    }

    // ─── GET EMPLOYEE'S ACTIVE INSURANCE ──────────────────────────────────────
    @Override
    public EmployeeInsuranceResponseDTO getEmployeeInsurance(Long employeeId) {

        EmployeeInsurance insurance = employeeInsuranceRepository
            .findByEmployee_UserIdAndStatus(employeeId, InsuranceStatus.ACTIVE)
            .orElseThrow(() -> new RuntimeException(
                "No active insurance found for employee: " + employeeId));

        return mapToInsuranceResponse(insurance);
    }

    // ─── MAPPERS ──────────────────────────────────────────────────────────────

    private InsurancePlanResponseDTO mapToPlanResponse(InsurancePlan plan) {
        InsurancePlanResponseDTO dto = new InsurancePlanResponseDTO();
        dto.setPlanId(plan.getId());
        dto.setPlanName(plan.getPlanName());
        dto.setCoverageAmount(plan.getCoverageAmount());
        dto.setDescription(plan.getDescription());
        dto.setIsActive(plan.getIsActive());
        dto.setCreatedBy(plan.getCreatedBy());
        dto.setCreatedAt(plan.getCreatedAt());
        return dto;
    }

    private EmployeeInsuranceResponseDTO mapToInsuranceResponse(
            EmployeeInsurance ins) {
        EmployeeInsuranceResponseDTO dto = new EmployeeInsuranceResponseDTO();
        dto.setEmployeeInsuranceId(ins.getId());
        dto.setEmployeeId(ins.getEmployee().getUserId());
        dto.setEmployeeName(
            ins.getEmployee().getFirstName() + " "
            + ins.getEmployee().getLastName());
        dto.setPlanName(ins.getInsurancePlan().getPlanName());
        dto.setCoverageAmount(ins.getInsurancePlan().getCoverageAmount());
        // shows live remaining coverage 
        dto.setRemainingCoverage(ins.getRemainingCoverage());
        dto.setAssignedDate(ins.getAssignedDate());
        dto.setExpiryDate(ins.getExpiryDate());
        dto.setStatus(ins.getStatus());
        dto.setCreatedAt(ins.getCreatedAt());
        return dto;
    }
}