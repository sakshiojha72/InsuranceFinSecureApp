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

public class InsurancePlan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "plan_id")
	private Long id;

    @Column(name = "plan_name", nullable = false, unique = true)
    private String planName;

    @Column(name = "coverage_amount", nullable = false)
    private Double coverageAmount; 

    @Column(name = "description")
    private String description;  

    @Column(name = "is_active")
    private Boolean isActive = true; 

    @Column(name = "created_by")
    private String createdBy; 

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    //new requirement for a default plan.
    @Column(name="is_default")
    private Boolean isDefault = false;
    
}