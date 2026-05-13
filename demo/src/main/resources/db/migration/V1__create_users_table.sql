CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL,
    refresh_token TEXT NULL,
    password_hash VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE (email)
);