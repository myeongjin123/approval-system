package com.portfolio.approval.domain.work.entity;

import com.portfolio.approval.domain.work.enums.WorkStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_history")
@Getter
@NoArgsConstructor
public class ApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long workItemId;

    @Column(nullable = false)
    private Long approverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkStatus toStatus;

    private String comment;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    @Builder
    public ApprovalHistory(Long workItemId, Long approverId, WorkStatus fromStatus, WorkStatus toStatus, String comment) {
        this.workItemId = workItemId;
        this.approverId = approverId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.comment = comment;
        this.processedAt = LocalDateTime.now();
    }
}
