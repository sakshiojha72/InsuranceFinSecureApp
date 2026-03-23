package com.ds.app.entity;

import com.ds.app.enums.EscalationStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Escalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who raised it — @JsonIgnoreProperties stops loop back to Employee.escalations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_emp_id")
    @JsonIgnoreProperties({"escalations", "appraisals", "company", "department", "project",
                            "hibernateLazyInitializer", "handler"})
    private Employee raisedBy;

    // who it is against
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_emp_id")
    @JsonIgnoreProperties({"escalations", "appraisals", "company", "department", "project",
                            "hibernateLazyInitializer", "handler"})
    private Employee targetEmployee;

    //  manager scope check (same dept + same company)
    // these are snapshots at time of raising 
    private Long departmentId;
    private Long companyId;

          // PERFORMANCE / ASSET_MISUSE — required field

    private String comment;

    @Enumerated(EnumType.STRING)
    private EscalationStatus status = EscalationStatus.OPEN;

    private LocalDateTime raisedAt = LocalDateTime.now();
    private LocalDateTime resolvedAt;   // null until resolved
}
