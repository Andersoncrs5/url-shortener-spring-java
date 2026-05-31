CREATE TABLE url_access_rule (
    id BIGINT PRIMARY KEY,

    url_id BIGINT NOT NULL,

    type VARCHAR(50) NOT NULL,

    rule_value VARCHAR(250) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    assigned_by_user_id BIGINT NOT NULL,

    expires_at TIMESTAMP NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_url_access_rule_url_id
        FOREIGN KEY (url_id)
        REFERENCES urls(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_url_access_rule_user_id
        FOREIGN KEY (assigned_by_user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_url_access_rule
        UNIQUE(url_id, type, rule_value)
);

CREATE INDEX idx_url_access_rule_url_id
    ON url_access_rule(url_id);

CREATE INDEX idx_url_access_rule_url_id_type
    ON url_access_rule(url_id, type);

CREATE INDEX idx_url_access_rule_expires_at
    ON url_access_rule(expires_at);