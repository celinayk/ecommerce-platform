package com.ecommerce.platform.domain.order.exception;

import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class OrderException extends RuntimeException {

  private final ErrorCode errorCode;

  public OrderException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public OrderException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}