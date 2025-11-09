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
  ORDER_ALREADY_CANCELLED(400, 3002, "이미 취소된 주문입니다."),

  // 카테고리 (4xxx)
  CATEGORY_NOT_FOUND(404, 4001, "존재하지 않는 카테고리입니다."),
  HAS_CHILD_CATEGORIES(400, 4002, "하위 카테고리가 있어 삭제할 수 없습니다."),
  DUPLICATE_RESOURCE(400, 4003, "이미 존재하는 리소스입니다."),

  // 환불 (5xxx)
  REFUND_NOT_FOUND(404, 5001, "존재하지 않는 환불 내역입니다."),
  REFUND_ALREADY_EXISTS(400, 5002, "이미 환불 요청된 주문입니다."),
  CANNOT_REFUND_CANCELED_ORDER(400, 5003, "취소된 주문은 환불할 수 없습니다."),
  UNAUTHORIZED_REFUND(403, 5004, "본인의 주문만 환불 요청할 수 있습니다."),
  INVALID_REFUND_AMOUNT(400, 5005, "환불 금액이 유효하지 않습니다."),
  REFUND_AMOUNT_EXCEEDED(400, 5006, "환불 금액이 주문 금액을 초과했습니다."),
  REFUND_PERIOD_EXPIRED(400, 5007, "환불 가능 기간이 지났습니다."),
  ORDER_NOT_COMPLETED(400, 5008, "완료된 주문만 환불 가능합니다."),
  REFUND_ALREADY_PROCESSED(400, 5009, "이미 처리된 환불 요청입니다.");

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
