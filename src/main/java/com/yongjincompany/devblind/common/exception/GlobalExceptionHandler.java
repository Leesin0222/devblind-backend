package com.yongjincompany.devblind.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity.status(code.getStatus()).body(Map.of(
                "code", code.name(),
                "message", code.getMessage(),
                "timestamp", System.currentTimeMillis()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().isEmpty() 
                ? "입력값이 올바르지 않습니다."
                : Objects.requireNonNullElse(
                    e.getBindingResult().getAllErrors().get(0).getDefaultMessage(), 
                    "입력값이 올바르지 않습니다."
                );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "code", ErrorCode.VALIDATION_ERROR.name(),
                "message", message,
                "timestamp", System.currentTimeMillis()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleEtc(Exception e) {
        log.error("예상치 못한 오류 발생: ", e);
        return ResponseEntity.status(500).body(Map.of(
                "code", ErrorCode.SERVER_ERROR.name(),
                "message", "서버 내부 오류가 발생했습니다.",
                "timestamp", System.currentTimeMillis()
        ));
    }
}

