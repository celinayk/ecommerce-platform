CREATE TABLE payments (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT        NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    status      VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                COMMENT 'PENDING, COMPLETED, CANCELED, REFUNDED, FAILED',
    method      VARCHAR(30)   NOT NULL
                COMMENT 'CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, VIRTUAL_ACCOUNT, MOBILE_PAYMENT',
    paid_at     DATETIME      NULL     COMMENT '결제 완료 일시',
    canceled_at DATETIME      NULL     COMMENT '취소 완료 일시',
    refunded_at DATETIME      NULL     COMMENT '환불 완료 일시',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT uq_payment_order UNIQUE (order_id)
);

CREATE INDEX idx_payments_order  ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);