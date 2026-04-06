package com.ds.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.ClaimStatus;
import com.ds.app.entity.InsuranceClaim;

@Repository
public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, Long> {
	//all claims of an emp 
    List<InsuranceClaim> findByEmployee_UserId(Long employeeId);
	
	//get claims categorised on status
    List<InsuranceClaim> findByStatus(ClaimStatus status);
    
    //to check if emp has a claim open already
    boolean existsByEmployee_UserIdAndStatus(Long userId, ClaimStatus status);
    
    // find a specific pending claim for an employee
    Optional<InsuranceClaim> findByEmployee_UserIdAndStatus(Long employeeId, ClaimStatus status);

    // get all claims raised against a specific insurance 
    List<InsuranceClaim> findByEmployeeInsurance_Id(Long employeeInsuranceId);
 

	 // Query 4 — pending claims with pagination
	 @Query("SELECT ic FROM InsuranceClaim ic " +
	        "WHERE ic.status = :status " +
	        "ORDER BY ic.raisedAt ASC")
	 List<InsuranceClaim> findAllPendingClaims(ClaimStatus status);


}
