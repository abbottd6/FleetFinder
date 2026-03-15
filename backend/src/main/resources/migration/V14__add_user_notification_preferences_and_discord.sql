ALTER TABLE users
    ADD COLUMN discord_user_id               VARCHAR(20) NULL,
    ADD COLUMN discord_username              VARCHAR(32) NULL,
    ADD COLUMN external_sys_notes_enabled    TINYINT(1)  NOT NULL DEFAULT 0,
    ADD COLUMN external_group_notes_enabled  TINYINT(1)  NOT NULL DEFAULT 0,
    ADD COLUMN external_social_notes_enabled TINYINT(1)  NOT NULL DEFAULT 0,

    ADD UNIQUE KEY uq_discord_user_id (discord_user_id);
