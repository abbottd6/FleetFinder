CREATE TABLE IF NOT EXISTS user_custom_notification
(
    id_custom_note BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    enabled        TINYINT(1)  NOT NULL DEFAULT 0,
    environment_id INT         NULL,
    experience_id  INT         NULL,
    category_id    INT         NULL,
    subcategory_id INT         NULL,
    language_code  VARCHAR(10) NULL,
    pvp_status_id  INT         NULL,
    keywords       VARCHAR(32) NULL,
    created_at     TIMESTAMP            DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_custom_note_to_user
        FOREIGN KEY (user_id) REFERENCES users (id_user),

    CONSTRAINT fk_custom_note_to_env
        FOREIGN KEY (environment_id) REFERENCES game_environment (environment_id),

    CONSTRAINT fk_custom_note_to_exp
        FOREIGN KEY (experience_id) REFERENCES game_experience (experience_id),

    CONSTRAINT fk_custom_note_to_category
        FOREIGN KEY (category_id) REFERENCES gameplay_category (category_id),

    CONSTRAINT fk_custom_note_to_subcategory
        FOREIGN KEY (subcategory_id) REFERENCES gameplay_subcategory (subcategory_id),

    CONSTRAINT fk_custom_note_to_pvp_status
        FOREIGN KEY (pvp_status_id) REFERENCES pvp_status (pvp_status_id)
);

CREATE TABLE IF NOT EXISTS new_listing_notify_queue
(
    id_group     BIGINT      NOT NULL PRIMARY KEY,
    status       VARCHAR(10) NOT NULL DEFAULT 'inQueue',
    queued_at    TIMESTAMP            DEFAULT CURRENT_TIMESTAMP NOT NULL,
    locked_at    TIMESTAMP   NULL,
    processed_at TIMESTAMP   NULL,

    CONSTRAINT fk_listing_notify_queue_to_group
        FOREIGN KEY (id_group) REFERENCES group_listing (id_group)
);