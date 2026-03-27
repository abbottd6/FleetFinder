CREATE TABLE IF NOT EXISTS user_custom_notification
(
    id_custom_note BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    enabled        TINYINT(1)  NOT NULL DEFAULT 0,
    tag_label      VARCHAR(32) NULL,
    server_id      INT         NULL,
    environment_id INT         NULL,
    experience_id  INT         NULL,
    category_id    INT         NULL,
    subcategory_id INT         NULL,
    system_id      INT         NULL,
    language_code  VARCHAR(10) NULL,
    pvp_status_id  INT         NULL,
    legality_id    INT         NULL,
    group_status_id INT        NULL,
    keywords       VARCHAR(32) NULL,
    created_at     TIMESTAMP            DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_custom_note_to_user
        FOREIGN KEY (user_id) REFERENCES users (id_user),

    CONSTRAINT fk_custom_note_to_server
        FOREIGN KEY (server_id) REFERENCES server_region (server_id),

    CONSTRAINT fk_custom_note_to_env
        FOREIGN KEY (environment_id) REFERENCES game_environment (environment_id),

    CONSTRAINT fk_custom_note_to_exp
        FOREIGN KEY (experience_id) REFERENCES game_experience (experience_id),

    CONSTRAINT fk_custom_note_to_category
        FOREIGN KEY (category_id) REFERENCES gameplay_category (category_id),

    CONSTRAINT fk_custom_note_to_subcategory
        FOREIGN KEY (subcategory_id) REFERENCES gameplay_subcategory (subcategory_id),

    CONSTRAINT fk_custom_note_to_system
        FOREIGN KEY (system_id) REFERENCES planetary_system (system_id),

    CONSTRAINT fk_custom_note_to_pvp_status
        FOREIGN KEY (pvp_status_id) REFERENCES pvp_status (pvp_status_id),

    CONSTRAINT fk_custom_note_to_legality
        FOREIGN KEY (legality_id) REFERENCES legality (legality_id),

    CONSTRAINT fk_custom_note_to_group_status
        FOREIGN KEY (group_status_id) REFERENCES group_status (group_status_id)
);

CREATE TABLE IF NOT EXISTS push_subscription
(
    id_push_sub          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT        NOT NULL,
    user_label           VARCHAR(32)   NOT NULL,
    device_url           VARCHAR(2048) NOT NULL,
    public_key           VARCHAR(255)  NOT NULL,
    browser_secret       VARCHAR(255)  NOT NULL,
    sys_notes_enabled    TINYINT(1)    NOT NULL DEFAULT 0,
    group_notes_enabled  TINYINT(1)    NOT NULL DEFAULT 1,
    social_notes_enabled TINYINT(1)    NOT NULL DEFAULT 1,
    daily_failure_count  INT           NOT NULL DEFAULT 0,
    created_at           TIMESTAMP              DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_push_sub_to_users
        FOREIGN KEY (user_id) REFERENCES users (id_user)
);

CREATE TABLE IF NOT EXISTS new_listing_notify_queue
(
    id_group     BIGINT      NOT NULL PRIMARY KEY,
    status       VARCHAR(10) NOT NULL,
    queued_at    TIMESTAMP            DEFAULT CURRENT_TIMESTAMP NOT NULL,
    locked_at    TIMESTAMP   NULL,
    processed_at TIMESTAMP   NULL,

    CONSTRAINT fk_listing_notify_queue_to_group
        FOREIGN KEY (id_group) REFERENCES group_listing (id_group) ON DELETE CASCADE,

    INDEX index_on_new_listing_queue_status_and_queued_at (status, queued_at)
);

CREATE TABLE IF NOT EXISTS notification_outbox_archive
(
    unsent_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    archived_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    outbox_id          BIGINT        NOT NULL,
    event_type         VARCHAR(128)  NULL,
    entity_type        VARCHAR(128)  NULL,
    entity_id          BIGINT        NULL,
    entity_owner_id    BIGINT        NULL,
    entity_new_status  VARCHAR(32)   NULL,
    payload_json       JSON          NULL,
    status             VARCHAR(32)   NULL,
    attempt_count      TINYINT       NULL,
    last_error         VARCHAR(2048) NULL,
    created_at         TIMESTAMP     NULL,
    locked_at          TIMESTAMP     NULL,
    sent_at            TIMESTAMP     NULL,
    delivery_channel   VARCHAR(32)   NULL,
    parent_entity_id   BIGINT        NULL,
    parent_entity_type VARCHAR(64)   NULL,
    sibling_key        CHAR(128)     NULL,
    error_count        TINYINT       NULL,
    push_sub_id        BIGINT        NULL,
    UNIQUE KEY uq_outbox_archive_on_outbox_id (outbox_id)
);