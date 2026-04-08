package com.ds.app.repository;

import com.ds.app.entity.Employee;
import com.ds.app.entity.Escalation;
import com.ds.app.enums.EscalationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface iEscalationRepository extends JpaRepository<Escalation, Long> {

    Page<Escalation> findByTargetEmployee(Employee target, Pageable pageable);
    Page<Escalation> findByStatus(EscalationStatus status, Pageable pageable);

    // internal use — to check open escalations before clearing flag
    List<Escalation> findByTargetEmployeeAndStatus(Employee target, EscalationStatus status);

    long countByStatus(EscalationStatus status);
    
    
 // finds all OPEN escalations raised before a given datetime
    List<Escalation> findByStatusAndRaisedAtBefore(
            EscalationStatus status,
            LocalDateTime cutoff);

}

