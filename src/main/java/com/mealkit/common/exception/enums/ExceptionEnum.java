package com.mealkit.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionEnum {
    DATABASE_ERROR("E000001", "DB 삽입/조회/수정/삭제 과정에서 오류가 발생했습니다."),
    DATA_NOT_FOUND("E000003", "요청한 데이터를 찾을 수 없습니다."),

    AUTHENTICATION_FAILED("E100001", "로그인 인증에 실패했습니다."),
    INVALID_TOKEN("E100002", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED("E100003", "토큰이 만료되었습니다."),
    ACCESS_DENIED("E100004", "권한이 없습니다."),

    INVALID_PARAMETER("E200001", "잘못된 요청 파라미터입니다."),
    MISSING_REQUIRED_FIELD("E200002", "필수 입력값이 누락되었습니다."),

    BUSINESS_LOGIC_FAILURE("E300001", "요청한 작업 처리 중 비즈니스 로직 오류가 발생했습니다.");

    private final String code;
    private final String message;
}
