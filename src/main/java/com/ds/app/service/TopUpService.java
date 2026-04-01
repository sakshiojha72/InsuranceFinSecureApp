package com.ds.app.service;

import java.util.List;

import com.ds.app.dto.request.BuyTopUpRequestDTO;
import com.ds.app.dto.request.CreateTopUpPlanRequestDTO;
import com.ds.app.dto.response.EmployeeTopUpResponseDTO;
import com.ds.app.dto.response.TopUpPlanResponseDTO;

public interface TopUpService {
	
	TopUpPlanResponseDTO createTopUpPlan(CreateTopUpPlanRequestDTO dto, String createdBy);
	
	List<TopUpPlanResponseDTO> getAllTopUpPlans();
	
	void deactivateTopUpPlan(Long topUpPlanId);
	
	EmployeeTopUpResponseDTO buyTopUp( BuyTopUpRequestDTO dto, Long employeeId);
	
	List<EmployeeTopUpResponseDTO> getEmployeeTopUps(Long employeeId);

}
