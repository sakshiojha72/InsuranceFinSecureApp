package com.ds.app.entity;

import com.ds.app.enums.AllocationAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AllocationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    private Long employeeId;
    private String employeeName;
    private Long companyId;
    private Long departmentId;  // nullable
    private Long projectId;     // nullable

    @Enumerated(EnumType.STRING)
    private AllocationAction action;    // ASSIGNED / DEALLOCATED

    private LocalDateTime actionAt = LocalDateTime.now();
    private Long performedById;         // HR or Admin userId
    private String performerName;
}
