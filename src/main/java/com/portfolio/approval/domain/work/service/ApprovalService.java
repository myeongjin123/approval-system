package com.portfolio.approval.domain.work.service;

import com.portfolio.approval.domain.user.entity.User;
import com.portfolio.approval.domain.user.enums.UserRole;
import com.portfolio.approval.domain.user.repository.UserRepository;
import com.portfolio.approval.domain.work.entity.ApprovalHistory;
import com.portfolio.approval.domain.work.entity.WorkItem;
import com.portfolio.approval.domain.work.enums.WorkStatus;
import com.portfolio.approval.domain.work.repository.ApprovalHistoryRepository;
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
public class ApprovalService {

    private final WorkItemRepository workItemRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final UserRepository userRepository;

    /**
     * 승인 요청: DRAFT → SUBMITTED
     * 신청자만 가능
     */
    @Transactional
    public WorkItem submit(Long workItemId, Long userId) {
        WorkItem workItem = getWorkItem(workItemId);
        User user = getUser(userId);

        validateRole(user, UserRole.APPLICANT);
        validateTransition(workItem.getStatus(), WorkStatus.SUBMITTED);

        WorkStatus fromStatus = workItem.getStatus();
        workItem.changeStatus(WorkStatus.SUBMITTED);

        recordHistory(workItem, userId, fromStatus, WorkStatus.SUBMITTED, "승인 요청");
        return workItemRepository.save(workItem);
    }

    /**
     * 1단계 검토 시작: SUBMITTED → STAGE1_REVIEW
     * 1단계 승인자만 가능
     */
    @Transactional
    public WorkItem startReview(Long workItemId, Long approverId) {
        WorkItem workItem = getWorkItem(workItemId);
        User approver = getUser(approverId);

        validateRole(approver, UserRole.STAGE1_APPROVER);
        validateTransition(workItem.getStatus(), WorkStatus.STAGE1_REVIEW);

        WorkStatus fromStatus = workItem.getStatus();
        workItem.changeStatus(WorkStatus.STAGE1_REVIEW);

        recordHistory(workItem, approverId, fromStatus, WorkStatus.STAGE1_REVIEW, "1단계 검토 시작");
        return workItemRepository.save(workItem);
    }

    /**
     * 승인 처리: 현재 단계에 따라 다음 상태로 전이
     * FSM 상태 전이 테이블에 따라 동적으로 처리
     */
    @Transactional
    public WorkItem approve(Long workItemId, Long approverId, String comment) {
        WorkItem workItem = getWorkItem(workItemId);
        User approver = getUser(approverId);

        WorkStatus currentStatus = workItem.getStatus();
        WorkStatus nextStatus = resolveApprovalTransition(currentStatus, approver.getRole());

        WorkStatus fromStatus = workItem.getStatus();
        workItem.changeStatus(nextStatus);

        recordHistory(workItem, approverId, fromStatus, nextStatus, comment);
        return workItemRepository.save(workItem);
    }

    /**
     * 반려 처리: 현재 검토 단계에서 REJECTED로 전이
     */
    @Transactional
    public WorkItem reject(Long workItemId, Long approverId, String comment) {
        WorkItem workItem = getWorkItem(workItemId);
        User approver = getUser(approverId);

        WorkStatus currentStatus = workItem.getStatus();
        validateRejectableStatus(currentStatus, approver.getRole());

        WorkStatus fromStatus = workItem.getStatus();
        workItem.changeStatus(WorkStatus.REJECTED);

        recordHistory(workItem, approverId, fromStatus, WorkStatus.REJECTED, comment);
        return workItemRepository.save(workItem);
    }

    public List<ApprovalHistory> getHistory(Long workItemId) {
        return approvalHistoryRepository.findByWorkItemIdOrderByProcessedAtAsc(workItemId);
    }

    // --- FSM 전이 테이블 ---

    private WorkStatus resolveApprovalTransition(WorkStatus current, UserRole role) {
        if (current == WorkStatus.STAGE1_REVIEW && role == UserRole.STAGE1_APPROVER) {
            return WorkStatus.STAGE1_APPROVED;
        }
        if (current == WorkStatus.STAGE1_APPROVED && role == UserRole.STAGE2_APPROVER) {
            return WorkStatus.STAGE2_REVIEW;
        }
        if (current == WorkStatus.STAGE2_REVIEW && role == UserRole.STAGE2_APPROVER) {
            return WorkStatus.APPROVED;
        }
        throw new BusinessException(ErrorCode.UNAUTHORIZED_APPROVER);
    }

    private void validateTransition(WorkStatus current, WorkStatus next) {
        boolean valid = switch (current) {
            case DRAFT -> next == WorkStatus.SUBMITTED;
            case SUBMITTED -> next == WorkStatus.STAGE1_REVIEW;
            case STAGE1_REVIEW -> next == WorkStatus.STAGE1_APPROVED || next == WorkStatus.REJECTED;
            case STAGE1_APPROVED -> next == WorkStatus.STAGE2_REVIEW;
            case STAGE2_REVIEW -> next == WorkStatus.APPROVED || next == WorkStatus.REJECTED;
            default -> false;
        };
        if (!valid) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION);
        }
    }

    private void validateRejectableStatus(WorkStatus current, UserRole role) {
        if (current == WorkStatus.STAGE1_REVIEW && role != UserRole.STAGE1_APPROVER) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_APPROVER);
        }
        if (current == WorkStatus.STAGE2_REVIEW && role != UserRole.STAGE2_APPROVER) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_APPROVER);
        }
        if (current != WorkStatus.STAGE1_REVIEW && current != WorkStatus.STAGE2_REVIEW) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION);
        }
    }

    private void recordHistory(WorkItem workItem, Long approverId, WorkStatus from, WorkStatus to, String comment) {
        ApprovalHistory history = ApprovalHistory.builder()
                .workItemId(workItem.getId())
                .approverId(approverId)
                .fromStatus(from)
                .toStatus(to)
                .comment(comment)
                .build();
        approvalHistoryRepository.save(history);
    }

    private WorkItem getWorkItem(Long id) {
        return workItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WORK_ITEM_NOT_FOUND));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateRole(User user, UserRole required) {
        if (user.getRole() != required) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_APPROVER);
        }
    }
}
