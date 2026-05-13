package com.ds.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"employee", "employeeInsurance"})
@EqualsAndHashCode(exclude = {"employee", "employeeInsurance"})
public class InsuranceClaim {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "claim_id")
	private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    // FK → Employee table — who raised this claim
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_insurance_id", nullable = false)
    // FK → employee_insurances table
    private EmployeeInsurance employeeInsurance;

    @Column(name = "claim_amount", nullable = true)
    private Double claimAmount; 

    @Column(name = "base_amount", nullable = true)
    private Double baseAmount; 
    
    @Column(name = "reason", nullable = false)
    private String reason; 

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatus status; 

    @Column(name = "raised_at")
    private LocalDateTime raisedAt; 

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt; 

    @Column(name = "resolved_by")
    private String resolvedBy; 

    @Column(name = "admin_remarks")
    private String adminRemarks; 
    
    

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}