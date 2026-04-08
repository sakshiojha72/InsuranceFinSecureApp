package com.ds.app.dto.response;

import com.ds.app.enums.AllocationAction;
import lombok.Data;

import java.time.LocalDateTime;

// Returned by API for allocation history — shows the full movement timeline of an employee
@Data
public class AllocationHistoryResponseDTO {

    private Long id;

    private Long employeeId;        // who moved

    private Long companyId;         // company at time of action
    private Long departmentId;      // dept at time of action
    private Long projectId;         // project at time of action (nullable)

    private AllocationAction action; // ASSIGNED / DEALLOCATED

    private LocalDateTime actionAt;

    private Long performedById;     // HR or Admin who did this
}
