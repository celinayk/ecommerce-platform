package com.ecommerce.platform.domain.category.exception;

import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class CategoryException extends RuntimeException {

  private final ErrorCode errorCode;

  public CategoryException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public CategoryException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}