package com.ds.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.TopUpPlan;
import java.util.List;


@Repository
public interface TopUpPlanRepository extends JpaRepository<TopUpPlan, Long>{

	
	Optional<TopUpPlan> findByTopUpName(String topUpName);
	
	//all acive top up plans for employees to browse 
	List<TopUpPlan> findByIsActiveTrue();
	
	//for duplicate name 
	boolean existsByTopUpName(String topUpName);
	
	
}
