package com.ds.app.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ds.app.dto.request.AssignInsuranceRequestDTO;
import com.ds.app.dto.request.CreateInsurancePlanRequestDTO;
import com.ds.app.dto.response.DeactivatePlanResponseDTO;
import com.ds.app.dto.response.EmployeeInsuranceResponseDTO;
import com.ds.app.dto.response.InsurancePlanResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeInsurance;
import com.ds.app.entity.InsurancePlan;
import com.ds.app.entity.InsuranceStatus;
import com.ds.app.exception.EmployeeNotFoundException;
import com.ds.app.exception.InsuranceAlreadyAssignedException;
import com.ds.app.exception.InsurancePlanNotFoundException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.EmployeeInsuranceRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.InsurancePlanRepository;
import com.ds.app.service.EmailService;
import com.ds.app.service.InsurancePlanService;

import jakarta.transaction.Transactional;

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
        List<InsurancePlan> plans = insurancePlanRepository.findAll();
        List<InsurancePlanResponseDTO> result = new ArrayList<>();

        for (InsurancePlan plan : plans) {
            result.add(mapToPlanResponse(plan));
        }
        return result;
    }

    // ─── DEACTIVATE PLAN ──────────────────────────────────────────────────────
    @Override
    @Transactional // all DB changes here must succeed together or all roll back
    public DeactivatePlanResponseDTO deactivateInsurancePlan(Long planId) {

        // 1. Find the plan being deactivated
        InsurancePlan planToDeactivate = insurancePlanRepository.findById(planId)
                .orElseThrow(() -> new InsurancePlanNotFoundException(planId));

        if (!planToDeactivate.getIsActive()) {
            throw new RuntimeException("This plan is already deactivated.");
        }

        // 2. Can't deactivate the default plan — employees would have nowhere to go
        if (Boolean.TRUE.equals(planToDeactivate.getIsDefault())) {
            throw new RuntimeException(
                "Cannot deactivate the default plan. "
                + "Please mark another plan as default first, then deactivate this one.");
        }

        // 3. Find the default plan — employees will move here
        InsurancePlan defaultPlan = insurancePlanRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new RuntimeException(
                    "No default plan is set. "
                    + "Please mark an active plan as default before deactivating any plan."));

        // 4. Soft-delete the plan being deactivated
        planToDeactivate.setIsActive(false);
        insurancePlanRepository.save(planToDeactivate);

        // 5. Find all employees currently on this plan (ACTIVE or EXPIRING_SOON)
        // Both statuses mean the employee currently has live coverage — we must handle both
        List<EmployeeInsurance> affectedRecords =
                employeeInsuranceRepository.findByInsurancePlan_IdAndStatusIn(
                        planId,
                        List.of(InsuranceStatus.ACTIVE, InsuranceStatus.EXPIRING_SOON));

        List<String> affectedNames = new ArrayList<>();

        // 6. For each affected employee — expire old record, create new one, send email
        for (EmployeeInsurance oldInsurance : affectedRecords) {

            Employee employee = oldInsurance.getEmployee();

            // Step A — expire the old insurance record (keep it for audit history)
            // We never delete insurance records — claims may reference them
            oldInsurance.setStatus(InsuranceStatus.EXPIRED);
            employeeInsuranceRepository.save(oldInsurance);

            // Step B — create a brand new EmployeeInsurance under the default plan
            EmployeeInsurance newInsurance = new EmployeeInsurance();
            newInsurance.setEmployee(employee);
            newInsurance.setInsurancePlan(defaultPlan);
            newInsurance.setAssignedDate(LocalDate.now());
            // keep their same expiry date — they shouldn't lose time they already paid for
            newInsurance.setExpiryDate(oldInsurance.getExpiryDate());
            newInsurance.setStatus(InsuranceStatus.ACTIVE);
            // remaining coverage resets to the new plan's coverage amount
            // (they get the default plan's full coverage — fair reassignment)
            newInsurance.setRemainingCoverage(defaultPlan.getCoverageAmount());
            newInsurance.setBaseAmount(defaultPlan.getCoverageAmount());
            // system did this assignment, not a specific admin user
            newInsurance.setAssignedBy("SYSTEM");
            employeeInsuranceRepository.save(newInsurance);

            // Step C — collect name for the response summary
            String fullName = employee.getFirstName() + " " + employee.getLastName();
            affectedNames.add(fullName);

            // Step D — send email notification
            // wrapped in try-catch so one bad email doesn't stop the whole loop
            String email = employee.getEmail();
            if (email != null && !email.isEmpty()) {
                try {
                    emailService.sendPlanDeactivatedAndReassignedEmail(
                            email,
                            fullName,
                            planToDeactivate.getPlanName(), // old plan name
                            defaultPlan.getPlanName(),      // new plan name
                            defaultPlan.getCoverageAmount(),
                            oldInsurance.getExpiryDate().toString());
                } catch (Exception e) {
                    // log it but don't fail the whole operation for one email error
                    System.err.println("Email failed for " + email + ": " + e.getMessage());
                }
            }
        }

        // 7. Build the summary response for the admin
        String message = affectedRecords.isEmpty()
                ? "Plan deactivated. No employees were on this plan."
                : affectedRecords.size() + " employee(s) have been moved to \""
                  + defaultPlan.getPlanName() + "\" and notified by email.";

        return new DeactivatePlanResponseDTO(
                planToDeactivate.getPlanName(),
                defaultPlan.getPlanName(),
                affectedRecords.size(),
                affectedNames,
                message);
    }
    
    // ─── SET DEFAULT PLAN ─────────────────────────────────────────────────────
    // Admin calls this to mark which plan is the fallback for deactivations
    @Override
    @Transactional // both queries (clear old + set new) must succeed together or both roll back
    public InsurancePlanResponseDTO setDefaultPlan(Long planId) {

        InsurancePlan plan = insurancePlanRepository.findById(planId)
                .orElseThrow(() -> new InsurancePlanNotFoundException(planId));

        // can't set an inactive plan as default — makes no sense to reassign to a dead plan
        if (!plan.getIsActive()) {
            throw new RuntimeException(
                "Cannot set a deactivated plan as default. Choose an active plan.");
        }

        // Step 1 — clear whoever was default before (could be no one, that's fine)
        insurancePlanRepository.clearExistingDefault();

        // Step 2 — mark this plan as the new default
        plan.setIsDefault(true);
        InsurancePlan saved = insurancePlanRepository.save(plan);

        return mapToPlanResponse(saved);
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


     List<EmployeeInsurance> existing = employeeInsuranceRepository
             .findByEmployee_UserId(dto.getEmployeeId());

     boolean alreadyActive = existing.stream()
             .anyMatch(ei -> ei.getStatus() == InsuranceStatus.ACTIVE
                          || ei.getStatus() == InsuranceStatus.EXPIRING_SOON);

     if (alreadyActive) {
         throw new InsuranceAlreadyAssignedException(dto.getEmployeeId());
     }

        // expiry date must be in the future
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
        insurance.setRemainingCoverage(plan.getCoverageAmount());
        insurance.setBaseAmount(plan.getCoverageAmount());
        EmployeeInsurance saved = employeeInsuranceRepository.save(insurance);

        String employeeEmail = employee.getEmail();
        if (employeeEmail != null && !employeeEmail.isEmpty()) {
            emailService.sendInsuranceAssignedEmail(
                employeeEmail,
                employee.getFirstName() + " " + employee.getLastName(),
                plan.getPlanName(),
                plan.getCoverageAmount(),
                dto.getExpiryDate().toString());
        } else {
            System.out.println("No email on file for employee ID: "
                + employee.getUserId() + " — skipping notification");
        }

     return mapToInsuranceResponse(saved);
    }

    // ─── GET EMPLOYEE'S ACTIVE INSURANCE ──────────────────────────────────────
    @Override
    public EmployeeInsuranceResponseDTO getEmployeeInsurance(Long employeeId) {

        List<EmployeeInsurance> insurances = employeeInsuranceRepository
                .findByEmployee_UserId(employeeId);
        
        EmployeeInsurance insurance = insurances.stream()
                .filter(ei -> ei.getStatus() == InsuranceStatus.ACTIVE
                           || ei.getStatus() == InsuranceStatus.EXPIRING_SOON)
                .findFirst()
                .orElseGet(() -> insurances.stream()
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No insurance found for employee ID: " + employeeId)));
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
        dto.setIsDefault(plan.getIsDefault());

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
        dto.setRemainingCoverage(ins.getRemainingCoverage());
        
        //compute days till expiry
	     if (ins.getExpiryDate() != null) {
	         long days = java.time.temporal.ChronoUnit.DAYS.between(
	                 LocalDate.now(), ins.getExpiryDate());
	         dto.setDaysUntilExpiry(days); 
	     }

        return dto;
    }
}