-- Convert action_type from ENUM to VARCHAR. MODIFY COLUMN triggers a table rebuild
-- which strips the DEFAULT (MySQL bug), so set the DEFAULT separately as a
-- metadata-only operation that avoids the rebuild.
ALTER TABLE listing_archive
    MODIFY COLUMN action_type VARCHAR(16) NULL;
ALTER TABLE listing_archive
    ALTER COLUMN action_type SET DEFAULT 'None';
