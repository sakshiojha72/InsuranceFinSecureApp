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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    // FK → Employee table — who raised this claim
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_insurance_id", nullable = false)
    // FK → employee_insurances table — which insurance this claim is against
    private EmployeeInsurance employeeInsurance;

    @Column(name = "claim_amount", nullable = false)
    private Double claimAmount; 

    @Column(name = "reason", nullable = false)
    private String reason; 

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatus status; 

    @Column(name = "raised_at")
    private LocalDateTime raisedAt; // set in service when employee submits claim

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt; // set in service when admin approves or rejects

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