CREATE TABLE user_roles (
    id BIGINT PRIMARY KEY,

    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    CONSTRAINT fk_user_roles_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role_id
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_roles_user_role
    ON user_roles(role_id, user_id);

CREATE INDEX idx_user_roles_user
    ON user_roles(user_id);

CREATE INDEX idx_user_roles_role
    ON user_roles(role_id);

CREATE UNIQUE INDEX uk_user_roles_user_role
    ON user_roles(user_id, role_id);