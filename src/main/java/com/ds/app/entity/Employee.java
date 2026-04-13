package com.ds.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name="user_id")
public class Employee extends AppUser {


	private String firstName;
	private String lastName;
	
	@Column(name = "email", unique = true)
	private String email;
	
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<EmployeeInsurance> insurances;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<EmployeeTopUp> topUps;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<InsuranceClaim> insuranceClaims;
    
}