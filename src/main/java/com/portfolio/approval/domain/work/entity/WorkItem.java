package com.portfolio.approval.domain.work.entity;

import com.portfolio.approval.domain.work.enums.WorkStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_item")
@Getter
@NoArgsConstructor
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkStatus status;

    @Column(nullable = false)
    private Long applicantId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public WorkItem(String title, String content, Long applicantId) {
        this.title = title;
        this.content = content;
        this.applicantId = applicantId;
        this.status = WorkStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
    }

    public void changeStatus(WorkStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
