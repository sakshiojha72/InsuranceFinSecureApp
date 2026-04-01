package com.ds.app.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
import com.ds.app.repository.EmployeeInsuranceRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.EmployeeTopUpRepository;
import com.ds.app.repository.TopUpPlanRepository;
import com.ds.app.service.TopUpService;

@Service
public class TopUpServiceImpl implements TopUpService{

	@Autowired
	private TopUpPlanRepository topUpPlanRepository;
	
	@Autowired
	private EmployeeTopUpRepository employeeTopUpRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private EmployeeInsuranceRepository employeeInsuranceRepository;
	
	
	
	@Override
	public TopUpPlanResponseDTO createTopUpPlan(CreateTopUpPlanRequestDTO dto, String createdBy) {

		//1. no duplicate top up 
		if(topUpPlanRepository.existsByTopUpName(dto.getTopUpName()))
		{
			throw new RuntimeException(
					"Top-up plan with this name already exists");
		}
		
		TopUpPlan topUp= new TopUpPlan();
        topUp.setTopUpName(dto.getTopUpName());
        topUp.setAdditionalCoverage(dto.getAdditionalCoverage());
        topUp.setPrice(dto.getPrice());
        topUp.setDescription(dto.getDescription());
        topUp.setCreatedBy(createdBy);
        topUp.setIsActive(true);

        TopUpPlan saved = topUpPlanRepository.save(topUp);
        return mapToTopUpPlanResponse(saved);

	}

	@Override
	public List<TopUpPlanResponseDTO> getAllTopUpPlans() {
		 return topUpPlanRepository.findByIsActiveTrue()
	                .stream()
	                .map(this::mapToTopUpPlanResponse)
	                .collect(Collectors.toList());
	}

	@Override
	public void deactivateTopUpPlan(Long topUpPlanId) {
        TopUpPlan topUp = topUpPlanRepository.findById(topUpPlanId)
                .orElseThrow(() -> new RuntimeException(
                    "Top-up plan not found with id: " + topUpPlanId
                ));

        // soft delete
        topUp.setIsActive(false);
        topUpPlanRepository.save(topUp);

	}

	@Override
	public EmployeeTopUpResponseDTO buyTopUp(BuyTopUpRequestDTO dto, Long employeeId) {
		
		//1. employee must exist
		Employee employee= employeeRepository.findById(employeeId)
				.orElseThrow(()-> new RuntimeException(
						"Employee not found"));
		//2. top up plan must exist 
		TopUpPlan topUpPlan = topUpPlanRepository.findById(dto.getTopUpPlanId())
				.orElseThrow(()-> new RuntimeException(
						"Top up plan not found"));
		//3. top up plan must be active
		if(!topUpPlan.getIsActive())
		{
			throw new RuntimeException(
					"This top-Up plan is no longer available for purchase");
		}
		
		//4. emp must have an active base insurance
        if (!employeeInsuranceRepository.existsByEmployee_UserIdAndStatus(
                employeeId, InsuranceStatus.ACTIVE)) {
            throw new RuntimeException(
                "Employee must have an active base insurance before buying a top-up"
            );
        }
       
        //5. employee can't buy same topUp twice
        if(employeeTopUpRepository.existsByEmployee_UserIdAndTopUpPlan_Id(employeeId, dto.getTopUpPlanId()))
        {
        	throw new RuntimeException(
        			"Employee has already purchased this top-up plan");
        }
        
        //all checks passed
        EmployeeTopUp purchase = new EmployeeTopUp();
        purchase.setEmployee(employee);
        purchase.setTopUpPlan(topUpPlan);
        purchase.setPurchasedDate(LocalDate.now());
        purchase.setExpiryDate(dto.getExpiryDate());
        purchase.setStatus(InsuranceStatus.ACTIVE);

        EmployeeTopUp saved = employeeTopUpRepository.save(purchase);
        return mapToTopUpResponse(saved);	
	}

	@Override
	public List<EmployeeTopUpResponseDTO> getEmployeeTopUps(Long employeeId) {
      //full topUp history
		return employeeTopUpRepository.findByEmployee_UserId(employeeId)
                .stream()
                .map(this::mapToTopUpResponse)
                .collect(Collectors.toList());

	}
	
	//MAPPERS- entity to repsonse
	
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

    private EmployeeTopUpResponseDTO mapToTopUpResponse(
            EmployeeTopUp topUp) {
        EmployeeTopUpResponseDTO dto = new EmployeeTopUpResponseDTO();
        dto.setEmployeeTopUpId(topUp.getId());
        dto.setEmployeeId(topUp.getEmployee().getUserId());
        dto.setEmployeeName(topUp.getEmployee().getFirstName()+" "+topUp.getEmployee().getLastName());
        dto.setTopUpName(topUp.getTopUpPlan().getTopUpName());
        dto.setAdditionalCoverage(
            topUp.getTopUpPlan().getAdditionalCoverage()
        );
        dto.setPrice(topUp.getTopUpPlan().getPrice());
        dto.setPurchasedDate(topUp.getPurchasedDate());
        dto.setExpiryDate(topUp.getExpiryDate());
        dto.setStatus(topUp.getStatus());
        dto.setCreatedAt(topUp.getCreatedAt());
        return dto;
    }


}
