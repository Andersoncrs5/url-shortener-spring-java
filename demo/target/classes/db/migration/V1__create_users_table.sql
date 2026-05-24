CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL,
    refresh_token TEXT NULL,
    password_hash VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    attempts_login_failed INT NOT NULL DEFAULT 0,
    blocked_at TIMESTAMP NULL,
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

ALTER TABLE users
ADD CONSTRAINT uk_users_email UNIQUE (email);

ALTER TABLE users
ADD CONSTRAINT uk_users_name UNIQUE (name);