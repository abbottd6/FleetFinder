ALTER TABLE users
    ADD COLUMN discord_user_id               VARCHAR(20) NULL,
    ADD COLUMN discord_username              VARCHAR(32) NULL,
    ADD COLUMN external_sys_notes_enabled    TINYINT(1)  NOT NULL DEFAULT 0,
    ADD COLUMN external_group_notes_enabled  TINYINT(1)  NOT NULL DEFAULT 0,
    ADD COLUMN external_social_notes_enabled TINYINT(1)  NOT NULL DEFAULT 0,

    ADD UNIQUE KEY uq_discord_user_id (discord_user_id);

ALTER TABLE notification
    ADD COLUMN outbox_id BIGINT NULL,
    ADD COLUMN delivery_channel VARCHAR(16) NOT NULL DEFAULT 'IN_APP',
    ADD COLUMN parent_entity_id BIGINT NULL,
    ADD COLUMN parent_entity_type VARCHAR(32) NULL,
    ADD COLUMN dropdown_priority TINYINT(1) NOT NULL DEFAULT 1,
    ADD COLUMN target_metadata JSON NULL,
    ADD COLUMN sibling_key CHAR(64) NULL,
    ADD CONSTRAINT fk_note_references_outbox_note
        FOREIGN KEY (outbox_id) REFERENCES notification_outbox (outbox_id);

ALTER TABLE notification_outbox
    ADD COLUMN delivery_channel VARCHAR(16) NOT NULL DEFAULT 'IN_APP',
    ADD COLUMN parent_entity_id BIGINT NULL,
    ADD COLUMN parent_entity_type VARCHAR(32) NULL,
    ADD COLUMN sibling_key CHAR(64) NULL,
    DROP INDEX uq_note_outbox_event,
    ADD UNIQUE KEY uq_note_outbox_event (event_type, entity_type, entity_id,
                                         entity_owner_id, entity_new_status, delivery_channel);

UPDATE notification
SET parent_entity_type = 'MOD_LISTING_ACTION',
    parent_entity_id = id_action
WHERE id_action IS NOT NULL;

ALTER TABLE notification
    DROP FOREIGN KEY note_fk_ref_mod_action,
    DROP COLUMN id_action;