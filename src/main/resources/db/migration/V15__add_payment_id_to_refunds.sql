ALTER TABLE refunds
    ADD COLUMN payment_id BIGINT NULL,
    ADD CONSTRAINT fk_refund_payment
        FOREIGN KEY (payment_id) REFERENCES payments(id);

CREATE INDEX idx_refunds_payment ON refunds(payment_id);