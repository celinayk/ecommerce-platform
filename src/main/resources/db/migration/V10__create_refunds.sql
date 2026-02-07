CREATE TABLE refunds (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         order_id BIGINT NOT NULL,
                         reason VARCHAR(500),
                         status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         CONSTRAINT fk_refund_user FOREIGN KEY (user_id) REFERENCES users(id),
                         CONSTRAINT fk_refund_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE INDEX idx_refunds_order ON refunds(order_id);
CREATE INDEX idx_refunds_user ON refunds(user_id);
CREATE INDEX idx_refunds_status ON refunds(status);