CREATE TABLE IF NOT EXISTS push_subscription
(
    id_push_sub          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT       NOT NULL,
    user_label           VARCHAR(32)  NOT NULL,
    device_url           TEXT         NOT NULL,
    public_key           VARCHAR(255) NOT NULL,
    browser_secret       VARCHAR(255) NOT NULL,
    sys_notes_enabled    TINYINT(1)   NOT NULL DEFAULT 0,
    group_notes_enabled  TINYINT(1)   NOT NULL DEFAULT 1,
    social_notes_enabled TINYINT(1)   NOT NULL DEFAULT 1,
    daily_failure_count  INT          NOT NULL DEFAULT 0,
    created_at           TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_push_sub_to_users
        FOREIGN KEY (user_id) REFERENCES users (id_user)
)