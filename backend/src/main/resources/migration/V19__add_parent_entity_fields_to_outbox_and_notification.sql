ALTER TABLE notification_outbox
    ADD COLUMN parent_entity_id BIGINT NULL,
    ADD COLUMN parent_entity_type VARCHAR(32) NULL;

ALTER TABLE notification
    ADD COLUMN parent_entity_id BIGINT NULL,
    ADD COLUMN parent_entity_type VARCHAR(32) NULL;

UPDATE notification
    SET parent_entity_type = 'MOD_LISTING_ACTION',
        parent_entity_id = id_action
    WHERE id_action IS NOT NULL;

ALTER TABLE notification
    DROP FOREIGN KEY note_fk_ref_mod_action,
    DROP COLUMN id_action;