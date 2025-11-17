package com.ecommerce.platform.domain.product.exception;

import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class ProductException extends RuntimeException {

  private final ErrorCode errorCode;

  public ProductException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ProductException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}