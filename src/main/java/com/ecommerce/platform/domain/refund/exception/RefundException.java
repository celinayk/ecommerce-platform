package com.ecommerce.platform.domain.refund.exception;

import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class RefundException extends RuntimeException {

  private final ErrorCode errorCode;

  public RefundException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public RefundException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}