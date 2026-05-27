CREATE TABLE url_tag_links (
    id BIGINT PRIMARY KEY,

    url_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,

    sort_order SMALLINT,

    note VARCHAR(500),

    primary_tag BOOLEAN NOT NULL DEFAULT FALSE,

    created_by BIGINT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_url_tag_links_url
        FOREIGN KEY (url_id)
        REFERENCES urls(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_url_tag_links_tag
        FOREIGN KEY (tag_id)
        REFERENCES url_tags(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_url_tag_links_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT uk_url_tag_links_unique
        UNIQUE (url_id, tag_id)
);

CREATE INDEX idx_url_tag_links_url_id
    ON url_tag_links(url_id);

CREATE INDEX idx_url_tag_links_tag_id
    ON url_tag_links(tag_id);

CREATE INDEX idx_url_tag_links_created_by
    ON url_tag_links(created_by);