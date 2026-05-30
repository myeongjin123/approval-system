package com.portfolio.approval.domain.work.enums;

/**
 * 업무 승인 FSM 상태 정의
 * DRAFT → SUBMITTED → STAGE1_REVIEW → STAGE1_APPROVED → STAGE2_REVIEW → APPROVED
 *                                   ↘ REJECTED                        ↘ REJECTED
 */
public enum WorkStatus {
    DRAFT,           // 초안 작성 중
    SUBMITTED,       // 승인 요청됨
    STAGE1_REVIEW,   // 1단계 검토 중
    STAGE1_APPROVED, // 1단계 승인 완료
    STAGE2_REVIEW,   // 2단계 검토 중 (최종)
    APPROVED,        // 최종 승인 완료
    REJECTED         // 반려
}
