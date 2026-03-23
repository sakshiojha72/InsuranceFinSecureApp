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

public class TopUpPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "top_up_name", nullable = false, unique = true)
    private String topUpName; 

    @Column(name = "additional_coverage", nullable = false)
    private Double additionalCoverage; 

    @Column(name = "price", nullable = false)
    private Double price; 

    @Column(name = "description")
    private String description; 

    @Column(name = "is_active")
    private Boolean isActive = true; // false = soft deleted, employees cannot buy it anymore

    @Column(name = "created_by")
    private String createdBy; // email of admin who created this — audit trail

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
