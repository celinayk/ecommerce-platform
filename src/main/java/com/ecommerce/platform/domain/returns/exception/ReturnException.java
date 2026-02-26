package com.ecommerce.platform.domain.returns.exception;

import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class ReturnException extends RuntimeException {

  private final ErrorCode errorCode;

  public ReturnException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ReturnException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}