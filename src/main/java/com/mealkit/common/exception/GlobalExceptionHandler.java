package com.mealkit.common.exception;

import com.mealkit.common.dto.DefaultRes;
import com.mealkit.common.exception.enums.ExceptionEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonExceptions.class)
    public ResponseEntity<DefaultRes.ApiResponse<Object>> handleCommonException(CommonExceptions e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(DefaultRes.ApiResponse.builder()
                        .code(e.getCode())
                        .message(e.getMessage())
                        .result(e.getData())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DefaultRes.ApiResponse<Object>> handleAccessDenied(AccessDeniedException e) {
        CommonExceptions ex = new CommonExceptions(ExceptionEnum.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(DefaultRes.ApiResponse.builder()
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultRes.ApiResponse<Object>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().isEmpty()
                ? ExceptionEnum.INVALID_PARAMETER.getMessage()
                : e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(DefaultRes.ApiResponse.builder()
                        .code("VALIDATION_ERROR")
                        .message(message)
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultRes.ApiResponse<Object>> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        log.error("처리되지 않은 오류 path={}", request.getRequestURI(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DefaultRes.ApiResponse.builder()
                        .code("E999999")
                        .message("서버 오류가 발생했습니다.")
                        .build());
    }
}
