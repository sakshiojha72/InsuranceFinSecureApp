package com.ds.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.EmployeeInsurance;
import com.ds.app.entity.InsurancePlan;
import com.ds.app.entity.InsuranceStatus;

@Repository
public interface EmployeeInsuranceRepository extends JpaRepository<EmployeeInsurance, Long>{
	
	//find active insurance for an emp
	Optional<EmployeeInsurance> findByEmployee_UserIdAndStatus(Long employeeId, InsuranceStatus active);

	//get all insurance records ( active+expired)
    List<EmployeeInsurance> findByEmployee_UserId(Long employeeId);
	
	//check if emp has an active insurance (for assigning new insurance)
	boolean existsByEmployee_UserIdAndStatus(Long employeeId, InsuranceStatus active);

	
}
