package com.ds.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ds.app.entity.Appraisal;
import com.ds.app.entity.Employee;

public interface iAppraisalRepository extends JpaRepository<Appraisal,Long> {
	Page<Appraisal> findByEmployee(Employee employee,Pageable pageable);

}
