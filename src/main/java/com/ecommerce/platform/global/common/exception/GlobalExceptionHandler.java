package com.ecommerce.platform.global.common.exception;

import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<Map<String, Object>> handleCustomException(CustomException e) {
    log.error("CustomException: code={}, message={}", e.getErrorCode().getCode(), e.getMessage());

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now());
    errorResponse.put("httpStatus", e.getErrorCode().getHttpStatus());
    errorResponse.put("code", e.getErrorCode().getCode());
    errorResponse.put("message", e.getMessage());

    return ResponseEntity
        .status(e.getErrorCode().getHttpStatus())
        .body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
    log.error("MethodArgumentNotValidException: ", e);

    Map<String, Object> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now());
    errorResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
    errorResponse.put("code", "VALIDATION_ERROR");
    errorResponse.put("message", "입력값 검증에 실패했습니다.");
    errorResponse.put("errors", errors);

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleException(Exception e) {
    log.error("Exception: ", e);

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now());
    errorResponse.put("httpStatus", ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus());
    errorResponse.put("code", ErrorCode.INTERNAL_SERVER_ERROR.getCode());
    errorResponse.put("message", ErrorCode.INTERNAL_SERVER_ERROR.getMessage());

    return ResponseEntity
        .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
        .body(errorResponse);
  }
}