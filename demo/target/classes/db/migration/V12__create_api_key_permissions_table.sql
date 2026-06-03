CREATE TABLE api_key_permissions (
    id BIGINT PRIMARY KEY,
    api_key_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_api_key_permissions_api_key_id
        FOREIGN KEY (api_key_id)
        REFERENCES api_keys(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_api_key_permissions_permission_id
        FOREIGN KEY (permission_id)
        REFERENCES permissions(id)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX uk_api_key_permissions
    ON api_key_permissions(api_key_id, permission_id);

CREATE INDEX idx_api_key_permissions_api_key_id
    ON api_key_permissions(api_key_id);

CREATE INDEX idx_api_key_permissions_permission_id
    ON api_key_permissions(permission_id);