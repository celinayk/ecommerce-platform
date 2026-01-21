package com.ecommerce.platform.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 통일된 API 응답 형식
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private boolean success;
  private T data;
  private Error error;

  /**
   * 성공 응답 (데이터 없음)
   */
  public static ApiResponse<Void> ok() {
    return ApiResponse.<Void>builder()
        .success(true)
        .build();
  }

  /**
   * 성공 응답 (데이터 포함)
   */
  public static <T> ApiResponse<T> ok(T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .data(data)
        .build();
  }

  /**
   * 에러 응답
   */
  public static ApiResponse<?> error(String code, String message) {
    return ApiResponse.builder()
        .success(false)
        .error(Error.of(code, message))
        .build();
  }

  /**
   * 에러 응답 (ErrorCode 사용)
   */
  public static ApiResponse<?> error(ErrorCode errorCode) {
    return ApiResponse.builder()
        .success(false)
        .error(Error.of(errorCode.name(), errorCode.getMessage()))
        .build();
  }

  @Getter
  @Builder
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Error {
    private String code;
    private String message;

    public static Error of(String code, String message) {
      return Error.builder()
          .code(code)
          .message(message)
          .build();
    }
  }
}