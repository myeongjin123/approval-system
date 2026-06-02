package com.portfolio.approval.domain.work.repository;

import com.portfolio.approval.domain.work.entity.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {
    List<ApprovalHistory> findByWorkItemIdOrderByProcessedAtAsc(Long workItemId);
}
