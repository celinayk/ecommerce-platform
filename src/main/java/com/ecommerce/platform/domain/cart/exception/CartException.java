package com.ecommerce.platform.domain.cart.exception;

import com.ecommerce.platform.global.common.response.ErrorCode;
import lombok.Getter;

@Getter
public class CartException extends RuntimeException {

    private final ErrorCode errorCode;

    public CartException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CartException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}