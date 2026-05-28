CREATE TABLE url_redirect_rules (
    id BIGINT PRIMARY KEY,

    url_id BIGINT NOT NULL,

    country_code VARCHAR(2),
    region VARCHAR(100),

    continent ENUM(
        'AFRICA','ANTARCTICA','ASIA','EUROPE',
        'NORTH_AMERICA','SOUTH_AMERICA','OCEANIA'
    ),

    os ENUM(
        'WINDOWS','MACOS','LINUX','ANDROID','IOS',
        'IPADOS','CHROME_OS','TVOS','WATCHOS','OTHER'
    ),

    browser ENUM(
        'CHROME','CHROMIUM','EDGE','FIREFOX','SAFARI',
        'OPERA','BRAVE','VIVALDI','SAMSUNG_INTERNET',
        'UC_BROWSER','OTHER','BOT'
    ),

    match_type ENUM(
        'EXACT','PARTIAL','STARTS_WITH','ENDS_WITH',
        'CONTAINS','ANY','NOT'
    ) NOT NULL,

    redirect_url VARCHAR(2048) NOT NULL,

    priority INT NOT NULL DEFAULT 0,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    start_at TIMESTAMP NULL,
    end_at TIMESTAMP NULL,

    rule_hash CHAR(64) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_url_redirect_rules_url
        FOREIGN KEY (url_id)
        REFERENCES urls(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_url_redirect_rules_hash UNIQUE (url_id, rule_hash)
);

CREATE INDEX idx_url_redirect_rules_url_id ON url_redirect_rules(url_id);
CREATE UNIQUE INDEX uk_rule_hash ON url_redirect_rules(rule_hash);
CREATE INDEX idx_url_redirect_rules_rule_hash ON url_redirect_rules(rule_hash);
CREATE INDEX idx_url_redirect_rules_lookup ON url_redirect_rules(url_id, active, match_type);