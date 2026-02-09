package com.ecommerce.platform.domain.stock.exception;

import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class StockException extends RuntimeException {
  private final ErrorCode errorCode;

  public StockException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public StockException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
