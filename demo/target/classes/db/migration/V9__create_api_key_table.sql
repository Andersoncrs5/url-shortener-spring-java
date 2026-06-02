CREATE TABLE api_keys (
    id BIGINT PRIMARY KEY,

    user_id BIGINT NOT NULL,

    key_hash VARCHAR(255) NOT NULL,

    name VARCHAR(100) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    last_used_at TIMESTAMP NULL,

    expires_at TIMESTAMP NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_api_keys_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_api_keys_key_hash
        UNIQUE (key_hash),

    CONSTRAINT uk_api_keys_name
        UNIQUE (name)
);

CREATE INDEX idx_api_keys_name
    ON api_keys(name);
