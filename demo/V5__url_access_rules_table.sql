CREATE TABLE url_access_rules (
    id BIGINT PRIMARY KEY,

    url_id BIGINT NOT NULL,

    type ENUM(
        'PASSWORD',
        'COUNTRY_ALLOW',
        'COUNTRY_BLOCK',
        'IP_ALLOW',
        'IP_BLOCK',
        'USER_AGENT_BLOCK',
        'RATE_LIMIT',
        'REQUIRE_AUTH',
        'EXPIRES_AT',
        'MAX_CLICKS'
    ) NOT NULL,

    value VARCHAR(1000),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_by BIGINT,

    expires_at TIMESTAMP NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_url_access_rules_url
        FOREIGN KEY (url_id)
        REFERENCES urls(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_url_access_rules_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_url_access_rules_url_id
    ON url_access_rules(url_id);

CREATE INDEX idx_url_access_rules_type
    ON url_access_rules(type);