package com.ecommerce.platform.global.common.response;

public enum ErrorCode {
  // 공통 (0xxx)
  SUCCESS(200, 0, "요청이 성공했습니다."),
  INVALID_INPUT(400, 1, "잘못된 입력값입니다."),
  INTERNAL_SERVER_ERROR(500, 2, "서버 내부 오류가 발생했습니다."),

  // 회원 (1xxx)
  USER_NOT_FOUND(404, 1001, "존재하지 않는 회원입니다."),
  EMAIL_ALREADY_EXISTS(400, 1002, "이미 존재하는 이메일입니다."),
  PASSWORD_UNMATCHED(401, 1003, "이메일 또는 비밀번호가 올바르지 않습니다."),

  // 상품 (2xxx)
  PRODUCT_NOT_FOUND(404, 2001, "존재하지 않는 상품입니다."),
  OUT_OF_STOCK(400, 2002, "재고가 부족합니다."),

  // 주문 (3xxx)
  ORDER_NOT_FOUND(404, 3001, "존재하지 않는 주문입니다."),
  ORDER_ALREADY_CANCELLED(400, 3002, "이미 취소된 주문입니다.");

  private final int httpStatus;
  private final int code;
  private final String message;

  ErrorCode(int httpStatus, int code, String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
