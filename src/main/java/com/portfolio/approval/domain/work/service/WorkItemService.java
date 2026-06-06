package com.portfolio.approval.domain.work.service;

import com.portfolio.approval.domain.work.entity.WorkItem;
import com.portfolio.approval.domain.work.enums.WorkStatus;
import com.portfolio.approval.domain.work.repository.WorkItemRepository;
import com.portfolio.approval.global.exception.BusinessException;
import com.portfolio.approval.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkItemService {

    private final WorkItemRepository workItemRepository;

    @Transactional
    public WorkItem create(String title, String content, Long applicantId) {
        WorkItem workItem = WorkItem.builder()
                .title(title)
                .content(content)
                .applicantId(applicantId)
                .build();
        return workItemRepository.save(workItem);
    }

    public WorkItem findById(Long id) {
        return workItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORK_ITEM_NOT_FOUND));
    }

    public List<WorkItem> findAll() {
        return workItemRepository.findAll();
    }

    public List<WorkItem> findByStatus(WorkStatus status) {
        return workItemRepository.findByStatus(status);
    }
}
