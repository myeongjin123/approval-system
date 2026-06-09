package com.portfolio.approval.domain.work.controller;

import com.portfolio.approval.domain.work.entity.WorkItem;
import com.portfolio.approval.domain.work.enums.WorkStatus;
import com.portfolio.approval.domain.work.service.WorkItemService;
import com.portfolio.approval.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-items")
@RequiredArgsConstructor
@Tag(name = "업무 항목", description = "업무 항목 생성 및 조회 API")
public class WorkItemController {

    private final WorkItemService workItemService;

    @Operation(summary = "업무 항목 생성", description = "신청자가 새 업무 항목을 초안(DRAFT) 상태로 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<WorkItem>> create(@Valid @RequestBody CreateRequest request) {
        WorkItem workItem = workItemService.create(request.title(), request.content(), request.applicantId());
        return ResponseEntity.ok(ApiResponse.ok("업무 항목이 생성되었습니다.", workItem));
    }

    @Operation(summary = "업무 항목 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkItem>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(workItemService.findById(id)));
    }

    @Operation(summary = "업무 항목 목록 조회", description = "status 파라미터로 상태별 필터링 가능합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkItem>>> findAll(
            @RequestParam(required = false) WorkStatus status) {
        List<WorkItem> result = (status != null)
                ? workItemService.findByStatus(status)
                : workItemService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    public record CreateRequest(
            @NotBlank(message = "제목을 입력해주세요.") String title,
            String content,
            @NotNull(message = "신청자 ID를 입력해주세요.") Long applicantId
    ) {}
}
