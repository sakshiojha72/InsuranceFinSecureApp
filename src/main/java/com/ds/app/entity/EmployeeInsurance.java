package com.ds.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"employee", "insurancePlan"})
@EqualsAndHashCode(exclude = {"employee", "insurancePlan"})
public class EmployeeInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private InsurancePlan insurancePlan;

    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate; 

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate; 

    @Enumerated(EnumType.STRING)
    
    @Column(name = "status", nullable = false)
    private InsuranceStatus status;

    @Column(name = "assigned_by")
    private String assignedBy; 
    
    @Column(name = "renewed_at")
    private LocalDate renewedAt; 

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}