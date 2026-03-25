-- Convert action_type from ENUM to VARCHAR. MODIFY COLUMN triggers a table rebuild
-- which strips the DEFAULT (MySQL bug), so set the DEFAULT separately as a
-- metadata-only operation that avoids the rebuild.
ALTER TABLE listing_archive
    MODIFY COLUMN action_type ENUM('None', 'AutoMod', 'Manual') NOT NULL;
ALTER TABLE listing_archive
    ALTER COLUMN action_type SET DEFAULT 'None';

ALTER TABLE notification_outbox
    MODIFY COLUMN last_error TEXT NULL;


ALTER TABLE notification
    MODIFY COLUMN type VARCHAR(32) NOT NULL DEFAULT 'UNDEFINED',
    ADD INDEX idx_note_on_sibling_key (sibling_key, id_user);