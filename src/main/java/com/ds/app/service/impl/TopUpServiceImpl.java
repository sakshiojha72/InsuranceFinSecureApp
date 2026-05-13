package com.ds.app.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ds.app.dto.request.BuyTopUpRequestDTO;
import com.ds.app.dto.request.CreateTopUpPlanRequestDTO;
import com.ds.app.dto.response.EmployeeTopUpResponseDTO;
import com.ds.app.dto.response.TopUpPlanResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeInsurance;
import com.ds.app.entity.EmployeeTopUp;
import com.ds.app.entity.InsuranceStatus;
import com.ds.app.entity.TopUpPlan;
import com.ds.app.exception.EmployeeNotFoundException;
import com.ds.app.exception.TopUpPlanNotFoundException;
import com.ds.app.repository.EmployeeInsuranceRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.EmployeeTopUpRepository;
import com.ds.app.repository.TopUpPlanRepository;
import com.ds.app.service.TopUpService;

@Service
public class TopUpServiceImpl implements TopUpService {

    @Autowired
    private TopUpPlanRepository topUpPlanRepository;

    @Autowired
    private EmployeeTopUpRepository employeeTopUpRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeInsuranceRepository employeeInsuranceRepository;

    // ─── CREATE TOP-UP PLAN ───────────────────────────────────────────────────

    @Override
    public TopUpPlanResponseDTO createTopUpPlan(
            CreateTopUpPlanRequestDTO dto, String createdBy) {

        // RULE: no duplicate top-up plan names
        if (topUpPlanRepository.existsByTopUpName(dto.getTopUpName())) {
            throw new RuntimeException(
                "Top-up plan with this name already exists");
        }

        TopUpPlan topUp = new TopUpPlan();
        topUp.setTopUpName(dto.getTopUpName());
        topUp.setAdditionalCoverage(dto.getAdditionalCoverage());
        topUp.setPrice(dto.getPrice());
        topUp.setDescription(dto.getDescription());
        topUp.setCreatedBy(createdBy); // from JWT
        topUp.setIsActive(true);

        TopUpPlan saved = topUpPlanRepository.save(topUp);
        return mapToTopUpPlanResponse(saved);
    }

    // ─── GET ALL ACTIVE TOP-UP PLANS ──────────────────────────────────────────

    @Override
    public List<TopUpPlanResponseDTO> getAllTopUpPlans() {

        List<TopUpPlan> plans = topUpPlanRepository.findAll();
        List<TopUpPlanResponseDTO> result = new ArrayList<>();

        for (TopUpPlan plan : plans) {
            result.add(mapToTopUpPlanResponse(plan));
        }
        return result;
    }

    // ─── DEACTIVATE TOP-UP PLAN ───────────────────────────────────────────────

    @Override
    public void deactivateTopUpPlan(Long topUpPlanId) {

        TopUpPlan topUp = topUpPlanRepository.findById(topUpPlanId)
            .orElseThrow(() -> new TopUpPlanNotFoundException(topUpPlanId));

        if (!topUp.getIsActive()) {
            throw new RuntimeException(
                "Top-up plan is already deactivated");
        }

        // soft delete
        topUp.setIsActive(false);
        topUpPlanRepository.save(topUp);
    }

    // ─── BUY TOP-UP ───────────────────────────────────────────────────────────

    @Override
    public EmployeeTopUpResponseDTO buyTopUp(
            BuyTopUpRequestDTO dto, Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        TopUpPlan topUpPlan = topUpPlanRepository.findById(dto.getTopUpPlanId())
            .orElseThrow(() -> new TopUpPlanNotFoundException(
                dto.getTopUpPlanId()));

        if (!topUpPlan.getIsActive()) {
            throw new RuntimeException(
                "This top-up plan is no longer available for purchase");
        }

        EmployeeInsurance activeInsurance = employeeInsuranceRepository
            .findByEmployee_UserIdAndStatus(employeeId, InsuranceStatus.ACTIVE)
            .orElseThrow(() -> new RuntimeException(
                "Employee must have an active base insurance "
                + "before buying a top-up plan"));

        if (employeeTopUpRepository.existsByEmployee_UserIdAndTopUpPlan_Id(
                employeeId, dto.getTopUpPlanId())) {
            throw new RuntimeException(
                "You have already purchased this top-up plan. "
                + "You cannot buy the same top-up twice.");
        }

        if (dto.getExpiryDate() != null &&
                activeInsurance.getExpiryDate() != null &&
                dto.getExpiryDate().isAfter(
                    activeInsurance.getExpiryDate())) {
            throw new RuntimeException(
                "Top-up expiry date (" + dto.getExpiryDate()
                + ") cannot be after your base insurance expiry date ("
                + activeInsurance.getExpiryDate() + ")");
        }

        // create purchase record ────────────────────────
        EmployeeTopUp purchase = new EmployeeTopUp();
        purchase.setEmployee(employee);
        purchase.setTopUpPlan(topUpPlan);
        purchase.setPurchasedDate(LocalDate.now());
        purchase.setExpiryDate(dto.getExpiryDate());
        purchase.setStatus(InsuranceStatus.ACTIVE);

        EmployeeTopUp saved = employeeTopUpRepository.save(purchase);

        // ── update remainingCoverage on base insurance ─────────────
        double updatedRemaining =
            activeInsurance.getRemainingCoverage()
            + topUpPlan.getAdditionalCoverage();

        activeInsurance.setRemainingCoverage(updatedRemaining);
        employeeInsuranceRepository.save(activeInsurance);

        return mapToTopUpResponse(saved);
    }

    // ─── GET EMPLOYEE'S TOP-UP HISTORY ────────────────────────────────────────

    @Override
    public List<EmployeeTopUpResponseDTO> getEmployeeTopUps(Long employeeId) {

        List<EmployeeTopUp> topUps =
            employeeTopUpRepository.findByEmployee_UserId(employeeId);
        List<EmployeeTopUpResponseDTO> result = new ArrayList<>();

        for (EmployeeTopUp topUp : topUps) {
            result.add(mapToTopUpResponse(topUp));
        }
        return result;
    }

    // ─── MAPPERS ──────────────────────────────────────────────────────────────

    private TopUpPlanResponseDTO mapToTopUpPlanResponse(TopUpPlan plan) {
        TopUpPlanResponseDTO dto = new TopUpPlanResponseDTO();
        dto.setTopUpPlanId(plan.getId());
        dto.setTopUpName(plan.getTopUpName());
        dto.setAdditionalCoverage(plan.getAdditionalCoverage());
        dto.setPrice(plan.getPrice());
        dto.setDescription(plan.getDescription());
        dto.setIsActive(plan.getIsActive());
        dto.setCreatedAt(plan.getCreatedAt());
        return dto;
    }

    private EmployeeTopUpResponseDTO mapToTopUpResponse(EmployeeTopUp topUp) {
        EmployeeTopUpResponseDTO dto = new EmployeeTopUpResponseDTO();
        dto.setEmployeeTopUpId(topUp.getId());
        dto.setEmployeeId(topUp.getEmployee().getUserId());
        dto.setEmployeeName(
            topUp.getEmployee().getFirstName() + " "
            + topUp.getEmployee().getLastName());
        dto.setTopUpName(topUp.getTopUpPlan().getTopUpName());
        dto.setAdditionalCoverage(
            topUp.getTopUpPlan().getAdditionalCoverage());
        dto.setPrice(topUp.getTopUpPlan().getPrice());
        dto.setPurchasedDate(topUp.getPurchasedDate());
        dto.setExpiryDate(topUp.getExpiryDate());
        dto.setStatus(topUp.getStatus());
        dto.setCreatedAt(topUp.getCreatedAt());
        return dto;
    }
}