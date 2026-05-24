CREATE TABLE urls (
    id BIGINT PRIMARY KEY,

    version BIGINT NOT NULL DEFAULT 0,

    user_id BIGINT NOT NULL,

    short_code VARCHAR(20) NOT NULL,

    original_url TEXT NOT NULL,

    title VARCHAR(255),

    description VARCHAR(500),

    favicon_url VARCHAR(500),

    domain VARCHAR(120),

    status VARCHAR(30) NOT NULL,

    access_type VARCHAR(30) NOT NULL,

    password_hash VARCHAR(255),

    custom_alias BOOLEAN NOT NULL DEFAULT FALSE,

    deleted_at TIMESTAMP NULL,

    expires_at TIMESTAMP NULL,

    last_access_at TIMESTAMP NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_urls_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_urls_short_code
        UNIQUE(short_code)
);

CREATE INDEX idx_urls_user_id
    ON urls(user_id);

CREATE INDEX idx_urls_status
    ON urls(status);

CREATE INDEX idx_urls_expires_at
    ON urls(expires_at);

CREATE INDEX idx_urls_last_access_at
    ON urls(last_access_at);

CREATE INDEX idx_urls_created_at
    ON urls(created_at);

CREATE INDEX idx_urls_deleted_at
    ON urls(deleted_at);

CREATE INDEX idx_urls_domain
    ON urls(domain);

CREATE INDEX idx_urls_access_type
    ON urls(access_type);

CREATE INDEX idx_urls_user_status
    ON urls(user_id, status);

CREATE INDEX idx_urls_user_created
    ON urls(user_id, created_at);

CREATE INDEX idx_urls_shortcode_deleted
    ON urls(short_code, deleted_at);