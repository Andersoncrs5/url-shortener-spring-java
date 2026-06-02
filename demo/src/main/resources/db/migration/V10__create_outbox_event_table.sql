CREATE TABLE outbox_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id BIGINT NOT NULL,

    event_type VARCHAR(100) NOT NULL,

    payload JSON NOT NULL,

    status VARCHAR(30) NOT NULL,

    topic VARCHAR(100) NOT NULL,

    retry_count INT NOT NULL DEFAULT 0,

    error_message TEXT,

    next_retry_at TIMESTAMP NULL,

    processed_at TIMESTAMP NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_outbox_event_aggregate_id
            UNIQUE (id)
);

CREATE INDEX idx_outbox_status
    ON outbox_events(status);

CREATE INDEX idx_outbox_next_retry
    ON outbox_events(next_retry_at);

CREATE INDEX idx_outbox_aggregate
    ON outbox_events(aggregate_type, aggregate_id);

CREATE INDEX idx_outbox_processing
    ON outbox_events(status, next_retry_at);