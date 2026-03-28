ALTER TABLE notification_outbox
    DROP INDEX uq_note_outbox_event,
    ADD COLUMN do_not_duplicate TINYINT DEFAULT 1,
    MODIFY COLUMN push_sub_id BIGINT NULL DEFAULT NULL,
    ADD UNIQUE KEY uq_note_outbox_push_id (sibling_key, entity_owner_id, push_sub_id),
    ADD UNIQUE KEY uq_note_outbox_discord (sibling_key, entity_owner_id, delivery_channel, do_not_duplicate);
