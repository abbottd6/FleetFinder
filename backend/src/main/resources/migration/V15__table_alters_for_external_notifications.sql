ALTER TABLE users
    ADD COLUMN discord_user_id               VARCHAR(20) NULL,
    ADD COLUMN discord_username              VARCHAR(32) NULL,
    ADD COLUMN external_sys_notes_enabled    TINYINT(1)  NOT NULL DEFAULT 0,
    ADD COLUMN external_group_notes_enabled  TINYINT(1)  NOT NULL DEFAULT 0,
    ADD COLUMN external_social_notes_enabled TINYINT(1)  NOT NULL DEFAULT 0,

    ADD UNIQUE KEY uq_discord_user_id (discord_user_id);

ALTER TABLE notification
    MODIFY COLUMN type VARCHAR(32) NOT NULL DEFAULT 'UNDEFINED',
    ADD COLUMN outbox_id BIGINT NULL,
    ADD COLUMN delivery_channel VARCHAR(16) NOT NULL DEFAULT 'IN_APP',
    ADD COLUMN parent_entity_id BIGINT NULL,
    ADD COLUMN parent_entity_type VARCHAR(32) NULL,
    ADD COLUMN dropdown_priority TINYINT(1) NOT NULL DEFAULT 1,
    ADD COLUMN target_metadata JSON NULL,
    ADD COLUMN sibling_key CHAR(64) NULL,
    ADD INDEX idx_note_on_sibling_key (sibling_key, id_user),
    ADD UNIQUE KEY uq_note_on_outbox_note (outbox_id),
    ADD CONSTRAINT fk_note_references_outbox_note
        FOREIGN KEY (outbox_id) REFERENCES notification_outbox (outbox_id)
            ON DELETE SET NULL;

ALTER TABLE notification_outbox
    MODIFY COLUMN last_error VARCHAR(2048),
    ADD COLUMN delivery_channel VARCHAR(16) NOT NULL DEFAULT 'IN_APP',
    ADD COLUMN parent_entity_id BIGINT NULL,
    ADD COLUMN parent_entity_type VARCHAR(32) NULL,
    ADD COLUMN sibling_key CHAR(64) NULL,
    ADD COLUMN error_count TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN push_sub_id BIGINT NULL,
    DROP INDEX uq_note_outbox_event,
    ADD CONSTRAINT fk_note_outbox_to_push_sub
        FOREIGN KEY (push_sub_id) REFERENCES push_subscription (id_push_sub)
            ON DELETE SET NULL,
    ADD UNIQUE KEY uq_note_outbox_event (event_type, entity_type, entity_id,
                                         entity_owner_id, entity_new_status, delivery_channel);

ALTER TABLE mod_listing_action
    ADD COLUMN action_basis INT NULL,
    ADD CONSTRAINT fk_mod_action_to_report_basis
        FOREIGN KEY (action_basis) REFERENCES listing_report_basis (id_basis);

UPDATE notification
SET parent_entity_type = 'MOD_LISTING_ACTION',
    parent_entity_id = id_action
WHERE id_action IS NOT NULL;

ALTER TABLE notification
    DROP FOREIGN KEY note_fk_ref_mod_action,
    DROP COLUMN id_action;