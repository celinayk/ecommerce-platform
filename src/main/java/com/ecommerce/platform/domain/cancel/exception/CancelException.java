package com.ecommerce.platform.domain.cancel.exception;

import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class CancelException extends RuntimeException{

  private final ErrorCode errorCode;

  public CancelException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public CancelException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
