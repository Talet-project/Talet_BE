package com.talet.talet.exception;

import com.talet.talet.util.TaletApiResponse;
import com.talet.talet.util.ErrorEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /* ====== 비즈니스(Custom) 예외 ====== */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<TaletApiResponse<Object>> handleCustomException(CustomException e) {
        ErrorEnum ec = e.getErrorCode();
        return ResponseEntity.status(ec.getStatus())
                .body(TaletApiResponse.error(ec.getCode(), ec.getStatus(), e.getMessage()));
    }

    /* ====== 그 외 처리되지 않은 예외 ====== */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<TaletApiResponse<Object>> handleException(Exception e) {
        ErrorEnum ec = ErrorEnum.COMMON_INTERNAL_ERROR;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(TaletApiResponse.error(ec.getCode(), ec.getStatus(), ec.getMessage()));
    }
}
