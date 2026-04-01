package com.ds.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.InsurancePlan;

import java.util.List;


@Repository
public interface InsurancePlanRepository extends JpaRepository<InsurancePlan, Long> {

	//to check duplicates before creating 
	Optional<InsurancePlan> findByPlanName(String planName);
	
	//get all active plans
	List<InsurancePlan> findByIsActiveTrue();
	
	//if plan name already exists
	boolean existsByPlanName(String planName);
	
}
