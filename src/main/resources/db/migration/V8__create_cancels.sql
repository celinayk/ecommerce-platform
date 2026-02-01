CREATE TABLE cancels (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         order_id BIGINT NOT NULL,
                         status VARCHAR(20) NOT NULL COMMENT 'REQUESTED, APPROVED, COMPLETED, REJECTED',
                         reason VARCHAR(200),
                         requested_by BIGINT NOT NULL,
                         approved_by BIGINT COMMENT 'NULL이면 자동 승인',
                         requested_at DATETIME NOT NULL,
                         approved_at DATETIME,
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_cancel_order FOREIGN KEY (order_id) REFERENCES orders(id),
                         CONSTRAINT fk_cancel_requested_by FOREIGN KEY (requested_by) REFERENCES users(id),
                         CONSTRAINT fk_cancel_approved_by FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE INDEX idx_cancels_order ON cancels(order_id);