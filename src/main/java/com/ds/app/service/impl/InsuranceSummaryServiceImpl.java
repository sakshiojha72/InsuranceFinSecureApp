package com.ds.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ds.app.dto.response.EmployeeTopUpResponseDTO;
import com.ds.app.dto.response.InsuranceSummaryDTO;
import com.ds.app.dto.response.TopUpPlanResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeInsurance;
import com.ds.app.entity.EmployeeTopUp;
import com.ds.app.entity.InsuranceStatus;
import com.ds.app.repository.EmployeeInsuranceRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.EmployeeTopUpRepository;
import com.ds.app.service.InsuranceSummaryService;

import lombok.RequiredArgsConstructor;

@Service
public class InsuranceSummaryServiceImpl implements InsuranceSummaryService {

	@Autowired
    private  EmployeeRepository employeeRepository;
    @Autowired
	private EmployeeInsuranceRepository employeeInsuranceRepository;
    @Autowired
    private EmployeeTopUpRepository employeeTopUpRepository;


    @Override
    public InsuranceSummaryDTO getInsuranceSummary(Long employeeId) {

        // employee must exist
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException(
                    "Employee not found with id: " + employeeId
                ));

        // active base insurance must exist
        EmployeeInsurance insurance = employeeInsuranceRepository
                .findByEmployee_UserIdAndStatus(employeeId, InsuranceStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException(
                    "No active insurance found for this employee"
                ));

        // fetch all active top-ups for this employee
        List<EmployeeTopUp> activeTopUps = employeeTopUpRepository
                .findByEmployee_UserIdAndStatus(
                    employeeId, InsuranceStatus.ACTIVE
                );

        // convert top-up entities to response DTOs
        List<EmployeeTopUpResponseDTO> topUpDTOs = 
        		activeTopUps.stream()
        		.map(topUp -> mapToTopUpResponse(topUp, employee))                
        		.collect(Collectors.toList());

        // TOTAL COVERAGE CALCULATION
        // base coverage + sum of all active top-up additional coverage
        Double totalCoverage =
            insurance.getInsurancePlan().getCoverageAmount()
            + activeTopUps.stream()
                .mapToDouble(t -> t.getTopUpPlan().getAdditionalCoverage())
                .sum();

        // full summary response
        InsuranceSummaryDTO summary = new InsuranceSummaryDTO();
        summary.setEmployeeId(employee.getUserId());
        summary.setEmployeeName(employee.getFirstName()+" "+employee.getLastName());
        summary.setEmployeeInsuranceId(insurance.getId());
        summary.setBasePlanName(
            insurance.getInsurancePlan().getPlanName()
        );
        summary.setBaseCoverageAmount(
            insurance.getInsurancePlan().getCoverageAmount()
        );
        summary.setExpiryDate(insurance.getExpiryDate());
        summary.setInsuranceStatus(insurance.getStatus());
        summary.setActiveTopUps(topUpDTOs);
        summary.setTotalCoverageAmount(totalCoverage);

       return summary;

    }
    
    private EmployeeTopUpResponseDTO mapToTopUpResponse(
            EmployeeTopUp topUp,
            Employee employee
    ) {

        EmployeeTopUpResponseDTO dto = new EmployeeTopUpResponseDTO();

        dto.setEmployeeTopUpId(topUp.getId());
        dto.setEmployeeId(employee.getUserId());
        dto.setEmployeeName(employee.getFirstName()+" "+employee.getLastName());
        dto.setTopUpName(topUp.getTopUpPlan().getTopUpName());
        dto.setAdditionalCoverage(topUp.getTopUpPlan().getAdditionalCoverage());
        dto.setPrice(topUp.getTopUpPlan().getPrice());
        dto.setPurchasedDate(topUp.getPurchasedDate());
        dto.setExpiryDate(topUp.getExpiryDate());
        dto.setStatus(topUp.getStatus());
        dto.setCreatedAt(topUp.getCreatedAt());

        return dto;
    }
     

    }


