package com.ds.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ds.app.dto.request.ClaimRequestDTO;
import com.ds.app.dto.request.ClaimStatusUpdateDTO;
import com.ds.app.dto.response.ClaimResponseDTO;
import com.ds.app.entity.ClaimStatus;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeInsurance;
import com.ds.app.entity.InsuranceClaim;
import com.ds.app.entity.InsuranceStatus;
import com.ds.app.exception.ClaimAlreadyProcessedException;
import com.ds.app.exception.EmployeeNotFoundException;
import com.ds.app.exception.EmployeeInsuranceNotFoundException;
import com.ds.app.exception.InsufficientCoverageException;
import com.ds.app.repository.EmployeeInsuranceRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.InsuranceClaimRepository;
import com.ds.app.service.InsuranceClaimService;

@Service
public class InsuranceClaimServiceImpl implements InsuranceClaimService {

    @Autowired
    private InsuranceClaimRepository insuranceClaimRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeInsuranceRepository employeeInsuranceRepository;

    // ─── RAISE CLAIM ──────────────────────────────────────────────────────────

    @Override
    public ClaimResponseDTO raiseClaim(ClaimRequestDTO dto, Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        EmployeeInsurance insurance = employeeInsuranceRepository
            .findById(dto.getEmployeeInsuranceId())
            .orElseThrow(() -> new EmployeeInsuranceNotFoundException(
                dto.getEmployeeInsuranceId()));

        if (!insurance.getEmployee().getUserId().equals(employeeId)) {
            throw new RuntimeException(
                "You can only raise claims on your own insurance");
        }

        if (insurance.getStatus() == InsuranceStatus.EXPIRED) {
            throw new RuntimeException(
                "Cannot raise a claim on an expired insurance policy");
        }

        if (insurance.getExpiryDate() != null &&
                insurance.getExpiryDate().isBefore(
                    java.time.LocalDate.now())) {
            // auto-mark as expired and save
            insurance.setStatus(InsuranceStatus.EXPIRED);
            employeeInsuranceRepository.save(insurance);
            throw new RuntimeException(
                "Your insurance policy has expired (expiry date: "
                + insurance.getExpiryDate() + "). Please contact HR.");
        }

        if (insuranceClaimRepository.existsByEmployee_UserIdAndStatus(
                employeeId, ClaimStatus.PENDING)) {
            throw new RuntimeException(
                "You already have a pending claim. "
                + "Wait for it to be resolved before raising a new one.");
        }

        //    claim amount must NOT exceed remaining coverage
        if (dto.getClaimAmount() <= 0) {
            throw new RuntimeException(
                "Claim amount must be greater than zero");
        }

        if (dto.getClaimAmount() > insurance.getRemainingCoverage()) {
            throw new InsufficientCoverageException(
                dto.getClaimAmount(), insurance.getRemainingCoverage());
        }

        // create the claim ─────────────────────────────
        InsuranceClaim claim = new InsuranceClaim();
        claim.setEmployee(employee);
        claim.setEmployeeInsurance(insurance);
        claim.setClaimAmount(dto.getClaimAmount());
        claim.setReason(dto.getReason());
        claim.setStatus(ClaimStatus.PENDING);
        claim.setRaisedAt(LocalDateTime.now());

        InsuranceClaim saved = insuranceClaimRepository.save(claim);
        return mapToClaimResponse(saved);
    }

    // ─── UPDATE CLAIM STATUS (HR/ADMIN) ───────────────────────────────────────

    @Override
    public ClaimResponseDTO updateClaimStatus(
            ClaimStatusUpdateDTO dto, String resolvedByUsername) {

        InsuranceClaim claim = insuranceClaimRepository
            .findById(dto.getClaimId())
            .orElseThrow(() -> new RuntimeException(
                "Claim not found with id: " + dto.getClaimId()));

        // RULE 1: only PENDING claims can be approved or rejected
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new ClaimAlreadyProcessedException(dto.getClaimId());
        }

        // RULE 2: status must move forward — can't set back to PENDING
        if (dto.getStatus() == ClaimStatus.PENDING) {
            throw new RuntimeException(
                "Cannot set claim status back to PENDING");
        }

        // RULE 3: admin remarks are mandatory 
        if (dto.getAdminRemarks() == null ||
                dto.getAdminRemarks().trim().isEmpty()) {
            throw new RuntimeException(
                "Admin remarks are mandatory when resolving a claim");
        }

        claim.setStatus(dto.getStatus());
        claim.setAdminRemarks(dto.getAdminRemarks());
        claim.setResolvedAt(LocalDateTime.now());

        claim.setResolvedBy(resolvedByUsername);

        //    business rule:
        //    remainingCoverage = coverageAmount + topUpCoverage - approvedClaimsTotal
        if (dto.getStatus() == ClaimStatus.APPROVED) {

            EmployeeInsurance insurance = claim.getEmployeeInsurance();
            double newRemaining =
                insurance.getRemainingCoverage() - claim.getClaimAmount();

            if (newRemaining < 0) {
                newRemaining = 0;
            }

            insurance.setRemainingCoverage(newRemaining);
            employeeInsuranceRepository.save(insurance);
        }

        InsuranceClaim saved = insuranceClaimRepository.save(claim);
        return mapToClaimResponse(saved);
    }

    // ─── GET CLAIMS BY EMPLOYEE ───────────────────────────────────────────────

    @Override
    public List<ClaimResponseDTO> getEmployeeClaims(Long employeeId) {

        List<InsuranceClaim> claims =
            insuranceClaimRepository.findByEmployee_UserId(employeeId);
        List<ClaimResponseDTO> result = new ArrayList<>();

        for (InsuranceClaim claim : claims) {
            result.add(mapToClaimResponse(claim));
        }
        return result;
    }

    // ─── GET ALL CLAIMS (ADMIN/HR) ─────────────────────────────────────────────

    @Override
    public List<ClaimResponseDTO> getAllClaims(ClaimStatus status) {

        List<InsuranceClaim> claims;

        if (status != null) {
            claims = insuranceClaimRepository.findByStatus(status);
        } else {
            claims = insuranceClaimRepository.findAll();
        }

        List<ClaimResponseDTO> result = new ArrayList<>();
        for (InsuranceClaim claim : claims) {
            result.add(mapToClaimResponse(claim));
        }
        return result;
    }

    // ─── MAPPER ───────────────────────────────────────────────────────────────

    private ClaimResponseDTO mapToClaimResponse(InsuranceClaim claim) {
        ClaimResponseDTO dto = new ClaimResponseDTO();
        dto.setClaimId(claim.getId());
        dto.setEmployeeId(claim.getEmployee().getUserId());
        dto.setEmployeeName(
            claim.getEmployee().getFirstName() + " "
            + claim.getEmployee().getLastName());
        dto.setEmployeeInsuranceId(
            claim.getEmployeeInsurance().getId());
        dto.setPlanName(
            claim.getEmployeeInsurance().getInsurancePlan().getPlanName());
        dto.setClaimAmount(claim.getClaimAmount());
        dto.setReason(claim.getReason());
        dto.setStatus(claim.getStatus());
        dto.setRaisedAt(claim.getRaisedAt());
        dto.setResolvedAt(claim.getResolvedAt());
        dto.setResolvedBy(claim.getResolvedBy());
        dto.setAdminRemarks(claim.getAdminRemarks());
        dto.setCreatedAt(claim.getCreatedAt());
        return dto;
    }
}