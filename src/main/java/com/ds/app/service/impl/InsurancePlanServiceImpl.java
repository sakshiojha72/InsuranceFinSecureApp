

package com.ds.app.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
import com.ds.app.repository.EmployeeInsuranceRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.InsurancePlanRepository;
import com.ds.app.service.InsurancePlanService;

@Service
public class InsurancePlanServiceImpl implements InsurancePlanService {

	@Autowired
	private InsurancePlanRepository insurancePlanRepository;
	
	@Autowired
	private EmployeeInsuranceRepository employeeInsuranceRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	//rule: no two plans with same name
	@Override
	public InsurancePlanResponseDTO createInsurancePlan(CreateInsurancePlanRequestDTO dto, String createdBy) {

		if(insurancePlanRepository.existsByPlanName(dto.getPlanName()))
		{
			throw new RuntimeException(
					"Insurance plan with this name already exists");
		}
		
		//entity from req DTO 
		InsurancePlan plan = new InsurancePlan();
		plan.setPlanName(dto.getPlanName());
        plan.setCoverageAmount(dto.getCoverageAmount());
        plan.setDescription(dto.getDescription());
        plan.setCreatedBy(createdBy); //from JWT 
        plan.setIsActive(true); // always active on creation

        InsurancePlan saved = insurancePlanRepository.save(plan);
        return mapToPlanResponse(saved);	
	}

	@Override
	public List<InsurancePlanResponseDTO> getAllInsurancePlans() {

		//only return active plans
		return insurancePlanRepository.findByIsActiveTrue()
				.stream()
				.map(plan->mapToPlanResponse(plan)) //converts each plan entity to dto 
				.collect(Collectors.toList());
				}

	@Override
	public void deactivateInsurancePlan(Long planId) {

		InsurancePlan plan = insurancePlanRepository.findById(planId)
				.orElseThrow(()-> new RuntimeException(
						"Insurance plan not founf with id: "+planId));
		
		//softdelete-set isActive to false
		plan.setIsActive(false);
		insurancePlanRepository.save(plan);
	}

	@Override
	public EmployeeInsuranceResponseDTO assignInsurance(AssignInsuranceRequestDTO dto) {

		//1. employee must exist in system 
		Employee employee= employeeRepository.findById(dto.getEmployeeId())
				.orElseThrow(()->new RuntimeException(
						"Employee not found with id:" +dto.getEmployeeId()));
		
		//2. plan must exist
		InsurancePlan plan = insurancePlanRepository.findById(dto.getPlanId())
				.orElseThrow(()->new RuntimeException(
						"Insurance plan not found with id: "+dto.getPlanId()));
		
		//3. cant assign a deactivated plan
		if(!plan.getIsActive())
		{
			throw new RuntimeException(
					"Cannot assign a decativated insurance plan");
		}
		//4. employee can have only one actice insurance at a time
		if(employeeInsuranceRepository.existsByEmployee_UserIdAndStatus(
			    dto.getEmployeeId(), InsuranceStatus.ACTIVE))
		{
			throw new RuntimeException(
					"Employee already has an active insurance plan");
		}
		
		//all check passed
		
		EmployeeInsurance insurance = new EmployeeInsurance();
		insurance.setEmployee(employee);
		insurance.setInsurancePlan(plan);
		insurance.setAssignedDate(LocalDate.now());
		insurance.setExpiryDate(dto.getExpiryDate());
		insurance.setStatus(InsuranceStatus.ACTIVE);
		
		
		EmployeeInsurance saved = employeeInsuranceRepository.save(insurance);
		return mapToInsuranceResponse(saved);		
	
	}
	
	

	@Override
	public EmployeeInsuranceResponseDTO getEmployeeInsurance(Long employeeId) {

		EmployeeInsurance insurance = employeeInsuranceRepository
				.findByEmployee_UserIdAndStatus(employeeId, InsuranceStatus.ACTIVE)
				.orElseThrow(()-> new RuntimeException(
						"No active insurance found for employee: "+ employeeId));
	
		return mapToInsuranceResponse(insurance);
	}
	
	
	
	//MAPPERS- converts entity to response DTO 
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
        dto.setEmployeInsuranceId(ins.getId());
        dto.setEmployeeId(ins.getEmployee().getUserId());
        dto.setEmployeeName(ins.getEmployee().getFirstName()+" "+ins.getEmployee().getLastName());
        dto.setPlanName(ins.getInsurancePlan().getPlanName());
        dto.setCoverageAmount(ins.getInsurancePlan().getCoverageAmount());
        dto.setAssignedDate(ins.getAssignedDate());
        dto.setExpiryDate(ins.getExpiryDate());
        dto.setStatus(ins.getStatus());
        dto.setCreatedAt(ins.getCreatedAt());
        return dto;
    }


}
