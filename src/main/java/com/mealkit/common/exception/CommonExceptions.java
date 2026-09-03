package com.mealkit.common.exception;

import com.mealkit.common.exception.enums.ExceptionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class CommonExceptions extends RuntimeException {

    protected String code;
    protected String message;
    private Object data;
    protected HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public CommonExceptions(ExceptionEnum exceptionCode) {
        super(exceptionCode.getMessage());
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }

    public CommonExceptions(ExceptionEnum exceptionCode, HttpStatus status) {
        super(exceptionCode.getMessage());
        this.httpStatus = status;
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }

    public CommonExceptions(ExceptionEnum exceptionCode, Object data) {
        super(exceptionCode.getMessage());
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
        this.data = data;
    }

    public CommonExceptions(String message, HttpStatus status) {
        super(message);
        this.httpStatus = status;
        this.code = "E999999";
        this.message = message;
    }
}
