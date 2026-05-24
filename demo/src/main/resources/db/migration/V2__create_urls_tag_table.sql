CREATE TABLE url_tags (
    id BIGINT PRIMARY KEY,

    user_id BIGINT NOT NULL,

    parent_id BIGINT NULL,

    name VARCHAR(120) NOT NULL,

    slug VARCHAR(140) NOT NULL,

    color VARCHAR(20),

    description VARCHAR(255),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_url_tags_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_url_tags_parent
        FOREIGN KEY (parent_id)
        REFERENCES url_tags(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_url_tag_slug UNIQUE(user_id, slug),
    CONSTRAINT uk_url_tag_name UNIQUE(user_id, name)
);

CREATE INDEX idx_url_tags_name
    ON url_tags(name);

CREATE INDEX idx_url_tags_slug
    ON url_tags(slug);

CREATE INDEX idx_url_tags_parent_id
    ON url_tags(parent_id);