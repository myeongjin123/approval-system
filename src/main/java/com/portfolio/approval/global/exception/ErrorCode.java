package com.portfolio.approval.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    WORK_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "업무 항목을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "현재 상태에서 허용되지 않는 전이입니다."),
    UNAUTHORIZED_APPROVER(HttpStatus.FORBIDDEN, "해당 단계의 승인 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
