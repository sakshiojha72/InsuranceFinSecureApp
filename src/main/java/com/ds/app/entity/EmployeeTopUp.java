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
@ToString(exclude = {"employee", "topUpPlan"})
@EqualsAndHashCode(exclude = {"employee", "topUpPlan"})
public class EmployeeTopUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    // FK → Employee table
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top_up_plan_id", nullable = false)
    // FK → top_up_plans table
    private TopUpPlan topUpPlan;

    @Column(name = "purchased_date")
    private LocalDate purchasedDate; // auto set when employee buys (will do in service layer)

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate; 
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InsuranceStatus status; 

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}