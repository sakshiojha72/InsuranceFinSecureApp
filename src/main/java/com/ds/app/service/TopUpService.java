package com.ds.app.service;

import java.util.List;

import com.ds.app.dto.request.BuyTopUpRequestDTO;
import com.ds.app.dto.request.CreateTopUpPlanRequestDTO;
import com.ds.app.dto.response.EmployeeTopUpResponseDTO;
import com.ds.app.dto.response.TopUpPlanResponseDTO;

public interface TopUpService {
	
	public TopUpPlanResponseDTO createTopUpPlan(CreateTopUpPlanRequestDTO dto, String createdBy);
	
	public List<TopUpPlanResponseDTO> getAllTopUpPlans();
	
	public void deactivateTopUpPlan(Long topUpPlanId);
	
	public EmployeeTopUpResponseDTO buyTopUp( BuyTopUpRequestDTO dto, Long employeeId);
	
	public List<EmployeeTopUpResponseDTO> getEmployeeTopUps(Long employeeId);

}
