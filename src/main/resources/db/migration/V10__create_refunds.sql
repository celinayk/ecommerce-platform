CREATE TABLE refunds (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         order_id BIGINT NOT NULL,
                         cancel_id BIGINT,
                         return_id BIGINT,
                         amount DECIMAL(10, 2) NOT NULL,
                         status VARCHAR(20) NOT NULL COMMENT 'PENDING, COMPLETED, FAILED',
                         refunded_at DATETIME,
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_refund_order FOREIGN KEY (order_id) REFERENCES orders(id),
                         CONSTRAINT fk_refund_cancel FOREIGN KEY (cancel_id) REFERENCES cancels(id),
                         CONSTRAINT fk_refund_return FOREIGN KEY (return_id) REFERENCES returns(id)
);

CREATE INDEX idx_refunds_order ON refunds(order_id);
