CREATE TABLE order_histories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    before_status VARCHAR(30),
    after_status VARCHAR(30) NOT NULL,
    changed_by BIGINT,
    reason VARCHAR(200),
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_history_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_history_user FOREIGN KEY (changed_by) REFERENCES users(id)
);

CREATE INDEX idx_order_histories_order ON order_histories(order_id);