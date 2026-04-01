package com.ds.app.service;

import java.util.List;

import com.ds.app.dto.request.ClaimRequestDTO;
import com.ds.app.dto.request.ClaimStatusUpdateDTO;
import com.ds.app.dto.response.ClaimResponseDTO;
import com.ds.app.entity.ClaimStatus;

public interface InsuranceClaimService {
    // employee raises a claim against their active insurance
    ClaimResponseDTO raiseClaim(ClaimRequestDTO dto, Long employeeId);

    // admin views all claims
    List<ClaimResponseDTO> getAllClaims(ClaimStatus status);

    // employee views their own claim history
    List<ClaimResponseDTO> getEmployeeClaims(Long employeeId);

    // admin approves or rejects a claim
    ClaimResponseDTO updateClaimStatus(ClaimStatusUpdateDTO dto);


}
