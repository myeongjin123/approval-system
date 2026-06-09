package com.portfolio.approval.domain.work.controller;

import com.portfolio.approval.domain.work.entity.ApprovalHistory;
import com.portfolio.approval.domain.work.entity.WorkItem;
import com.portfolio.approval.domain.work.service.ApprovalService;
import com.portfolio.approval.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
@Tag(name = "승인 처리", description = "업무 항목 승인/반려 처리 API")
public class ApprovalController {

    private final ApprovalService approvalService;

    @Operation(summary = "승인 요청", description = "신청자가 DRAFT → SUBMITTED 상태로 승인을 요청합니다.")
    @PostMapping("/{workItemId}/submit")
    public ResponseEntity<ApiResponse<WorkItem>> submit(
            @PathVariable Long workItemId,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("승인이 요청되었습니다.", approvalService.submit(workItemId, request.userId())));
    }

    @Operation(summary = "검토 시작", description = "1단계 승인자가 SUBMITTED → STAGE1_REVIEW 상태로 검토를 시작합니다.")
    @PostMapping("/{workItemId}/review")
    public ResponseEntity<ApiResponse<WorkItem>> startReview(
            @PathVariable Long workItemId,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("검토가 시작되었습니다.", approvalService.startReview(workItemId, request.userId())));
    }

    @Operation(summary = "승인", description = "현재 단계의 승인자가 다음 상태로 승인 처리합니다.")
    @PostMapping("/{workItemId}/approve")
    public ResponseEntity<ApiResponse<WorkItem>> approve(
            @PathVariable Long workItemId,
            @Valid @RequestBody ApprovalRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("승인 처리되었습니다.", approvalService.approve(workItemId, request.approverId(), request.comment())));
    }

    @Operation(summary = "반려", description = "현재 단계의 승인자가 업무 항목을 반려합니다.")
    @PostMapping("/{workItemId}/reject")
    public ResponseEntity<ApiResponse<WorkItem>> reject(
            @PathVariable Long workItemId,
            @Valid @RequestBody ApprovalRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("반려 처리되었습니다.", approvalService.reject(workItemId, request.approverId(), request.comment())));
    }

    @Operation(summary = "승인 이력 조회", description = "업무 항목의 전체 승인 이력을 시간순으로 조회합니다.")
    @GetMapping("/{workItemId}/history")
    public ResponseEntity<ApiResponse<List<ApprovalHistory>>> getHistory(@PathVariable Long workItemId) {
        return ResponseEntity.ok(ApiResponse.ok(approvalService.getHistory(workItemId)));
    }

    public record UserRequest(@NotNull(message = "사용자 ID를 입력해주세요.") Long userId) {}

    public record ApprovalRequest(
            @NotNull(message = "승인자 ID를 입력해주세요.") Long approverId,
            String comment
    ) {}
}
