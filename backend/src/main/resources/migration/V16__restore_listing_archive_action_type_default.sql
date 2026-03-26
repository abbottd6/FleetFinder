-- Convert action_type from ENUM to VARCHAR. MODIFY COLUMN triggers a table rebuild
-- which strips the DEFAULT (MySQL bug), so set the DEFAULT separately as a
-- metadata-only operation that avoids the rebuild.
ALTER TABLE listing_archive
    MODIFY COLUMN action_type ENUM('None', 'AutoMod', 'Manual') NOT NULL DEFAULT 'None';

ALTER TABLE notification_outbox
    MODIFY COLUMN last_error VARCHAR(2048),
    ADD COLUMN error_count TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN push_sub_id BIGINT NULL,
    ADD CONSTRAINT fk_note_outbox_to_push_sub
        FOREIGN KEY (push_sub_id) REFERENCES push_subscription (id_push_sub)
            ON DELETE SET NULL;

ALTER TABLE notification
    DROP FOREIGN KEY fk_note_references_outbox_note;

ALTER TABLE notification
    MODIFY COLUMN type VARCHAR(32) NOT NULL DEFAULT 'UNDEFINED',
    ADD CONSTRAINT fk_note_references_outbox_note
        FOREIGN KEY (outbox_id) REFERENCES notification_outbox (outbox_id)
            ON DELETE SET NULL,
    ADD INDEX idx_note_on_sibling_key (sibling_key, id_user);

ALTER TABLE push_subscription
    MODIFY COLUMN device_url VARCHAR(2048) NOT NULL;

ALTER TABLE new_listing_notify_queue
    DROP FOREIGN KEY fk_listing_notify_queue_to_group;

ALTER TABLE new_listing_notify_queue
    ADD CONSTRAINT fk_listing_notify_queue_to_group
        FOREIGN KEY (id_group) REFERENCES group_listing (id_group)
            ON DELETE CASCADE;

