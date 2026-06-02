package com.portfolio.approval.domain.work.repository;

import com.portfolio.approval.domain.work.entity.WorkItem;
import com.portfolio.approval.domain.work.enums.WorkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {
    List<WorkItem> findByStatus(WorkStatus status);
    List<WorkItem> findByApplicantId(Long applicantId);
}
