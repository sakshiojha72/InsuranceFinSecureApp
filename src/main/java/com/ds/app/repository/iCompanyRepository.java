package com.ds.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ds.app.entity.Company;

public interface iCompanyRepository extends JpaRepository<Company, Long> {
	
	Optional<Company> findByCode(String code);
	boolean existsByCode(String code);
	

}
