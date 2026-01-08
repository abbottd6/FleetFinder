CREATE TABLE IF NOT EXISTS conversation
(
    id_conversation      BIGINT PRIMARY KEY AUTO_INCREMENT,
    type                 ENUM ('DIRECT', 'GROUP') NOT NULL,
    title                VARCHAR(255)             NULL,
    initiated_by_user_id BIGINT                   NOT NULL,
    created_at           DATETIME                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME                 NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_msg_id          BIGINT                   NULL,
    dm_key               CHAR(64)                 NULL,

    CONSTRAINT conversation_fk_ref_initiating_user
        FOREIGN KEY (initiated_by_user_id) REFERENCES users (id_user),

    CONSTRAINT conversation_uq_direct_msg_key
        UNIQUE KEY (dm_key),

    INDEX conversation_idx_last_update (updated_at)
);

CREATE TABLE IF NOT EXISTS message
(
    id_msg                BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_conversation       BIGINT                  NOT NULL,
    id_sender             BIGINT                  NOT NULL,
    message_type          ENUM ('TEXT', 'SYSTEM') NOT NULL DEFAULT 'TEXT',
    msg_body              TEXT                    NOT NULL,
    created_at            DATETIME                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    edited_at             DATETIME                NULL,
    deleted_at            DATETIME                NULL,
    received_at           DATETIME                NULL,
    replied_to_message_id BIGINT                  NULL,
    client_message_id     CHAR(36)                NULL,
    metadata              JSON                    NULL,

    CONSTRAINT msg_fk_ref_conversation
        FOREIGN KEY (id_conversation) REFERENCES conversation (id_conversation),

    CONSTRAINT msg_fk_ref_sender
        FOREIGN KEY (id_sender) REFERENCES users (id_user),

    CONSTRAINT msg_fk_ref_replied_to_message
        FOREIGN KEY (replied_to_message_id) REFERENCES message (id_msg),

    CONSTRAINT msg_uq_client_message
        UNIQUE KEY (id_conversation, client_message_id),

    INDEX msg_idx_conv_id (id_conversation, id_msg),
    INDEX msg_idx_conv_created (id_conversation, created_at)
);

CREATE TABLE IF NOT EXISTS conversation_participant
(
    id_conversation          BIGINT                   NOT NULL,
    id_user                  BIGINT                   NOT NULL,
    role                     ENUM ('MEMBER', 'OWNER') NOT NULL DEFAULT 'MEMBER',
    joined_at                DATETIME                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at                  DATETIME                 NULL,
    last_read_message_id     BIGINT                   NULL,
    last_active_at           DATETIME                 NULL,
    is_archived              BOOLEAN                  NOT NULL DEFAULT FALSE,
    muting                   BOOLEAN                  NOT NULL DEFAULT FALSE,
    deleted_up_to_message_id BIGINT                   NULL,

    PRIMARY KEY (id_conversation, id_user),

    CONSTRAINT conv_participant_fk_ref_conversation
        FOREIGN KEY (id_conversation) REFERENCES conversation(id_conversation),

    CONSTRAINT conv_participant_fk_ref_user
        FOREIGN KEY (id_user) REFERENCES users(id_user),

    CONSTRAINT conv_participant_fk_ref_last_read_id
        FOREIGN KEY (last_read_message_id) REFERENCES message(id_msg),

    CONSTRAINT conv_participant_fk_ref_deleted_up_to
        FOREIGN KEY (deleted_up_to_message_id) REFERENCES message(id_msg),

    INDEX conv_participant_idx_on_user_and_conv (id_user, id_conversation),
    INDEX conv_participant_idx_on_user_and_last_active (id_user, last_active_at)
);

ALTER TABLE conversation
    ADD CONSTRAINT conversation_fk_ref_last_message
    FOREIGN KEY (last_msg_id) REFERENCES message (id_msg);