CREATE TABLE returns (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         order_id BIGINT NOT NULL,
                         order_item_id BIGINT NOT NULL,
                         status VARCHAR(20) NOT NULL COMMENT 'REQUESTED, APPROVED, PICKED_UP, INSPECTED, COMPLETED, REJECTED',
                         reason VARCHAR(200),
                         requested_by BIGINT NOT NULL,
                         approved_by BIGINT,
                         requested_at DATETIME NOT NULL,
                         approved_at DATETIME,
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_return_order FOREIGN KEY (order_id) REFERENCES orders(id),
                         CONSTRAINT fk_return_order_item FOREIGN KEY (order_item_id) REFERENCES order_items(id),
                         CONSTRAINT fk_return_requested_by FOREIGN KEY (requested_by) REFERENCES users(id),
                         CONSTRAINT fk_return_approved_by FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE INDEX idx_returns_order ON returns(order_id);