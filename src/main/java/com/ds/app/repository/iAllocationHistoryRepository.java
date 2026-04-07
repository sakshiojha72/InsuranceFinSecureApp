package com.ds.app.repository;

import com.ds.app.entity.AllocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface iAllocationHistoryRepository extends JpaRepository<AllocationHistory, Long> {
    // ordered by actionAt so caller gets chronological timeline
    List<AllocationHistory> findByEmployeeIdOrderByActionAtAsc(Long employeeId);
}

