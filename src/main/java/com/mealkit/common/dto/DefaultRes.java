package com.mealkit.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

/**
 * 공통 API 응답 래퍼 — 프론트는 data.code === '0000' 을 성공으로 본다.
 */
@Data
@AllArgsConstructor
@Builder
public class DefaultRes {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiResponse<T> {
        private String code;
        private String message;
        private T result;
    }

    public static ResponseEntity<ApiResponse<Void>> build() {
        return build(null);
    }

    public static <T> ResponseEntity<ApiResponse<T>> build(T obj) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .code("0000")
                        .message("Success")
                        .result(obj)
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> build(T obj, String msg) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .code("0000")
                        .message(msg)
                        .result(obj)
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> build(T obj, String msg, String code) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .code(code)
                        .message(msg)
                        .result(obj)
                        .build()
        );
    }

    public static ResponseEntity<ApiResponse<Void>> buildSuccess() {
        return build(null, "정상 처리되었습니다.");
    }

    public static ResponseEntity<ApiResponse<Void>> buildError(String message, String code) {
        return build(null, message, code);
    }
}
