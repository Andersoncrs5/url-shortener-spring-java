CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active TINYINT(1) DEFAULT 1,
    CONSTRAINT uk_roles_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    active TINYINT(1) DEFAULT 1,
    email_verified TINYINT(1) DEFAULT 0,
    blocked_at DATETIME DEFAULT NULL,
    roles VARCHAR(255) DEFAULT '',
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY,
    recipient VARCHAR(255) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    subject VARCHAR(255),
    body TEXT,
    status VARCHAR(50) NOT NULL,
    template_name VARCHAR(100),
    retry_count INT DEFAULT 0,
    provider_message_id VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,

    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,

    CONSTRAINT uk_user_roles_user_role UNIQUE (user_id, role_id)
);