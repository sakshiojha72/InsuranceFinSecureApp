package com.ds.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.InsurancePlan;

import java.util.List;


@Repository
public interface InsurancePlanRepository extends JpaRepository<InsurancePlan, Long> {

	//to check duplicates before creating 
	public Optional<InsurancePlan> findByPlanName(String planName);
	
	//get all active plans
	public List<InsurancePlan> findByIsActiveTrue();
	
	//if plan name already exists
	public boolean existsByPlanName(String planName);
	
	//New reqm- find plan marked as default
	Optional<InsurancePlan> findByIsDefaultTrue();
	
	//New reqm- before marking a new plan as default, clear the old default
	@Modifying
    @Query("UPDATE InsurancePlan p SET p.isDefault = false WHERE p.isDefault = true")
    void clearExistingDefault();
	//Only one plan can be default. When admin marks Plan B as default, we must first unmark Plan A
	
}
