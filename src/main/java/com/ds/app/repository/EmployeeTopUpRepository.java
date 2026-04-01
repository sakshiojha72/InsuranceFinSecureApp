package com.ds.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.EmployeeTopUp;
import com.ds.app.entity.InsuranceStatus;

@Repository
public interface EmployeeTopUpRepository extends JpaRepository<EmployeeTopUp, Long>{

	//get all top ups for an emp 
	List<EmployeeTopUp> findByEmployee_UserId(Long employeeId);
	
	//only active top ups for an emp 
	List<EmployeeTopUp> findByEmployee_UserIdAndStatus(Long employeeId, InsuranceStatus status);
	
	//check if emp already bought this topup 
	boolean existsByEmployee_UserIdAndTopUpPlan_Id(Long employeeId, Long topUpPlanId);
}
