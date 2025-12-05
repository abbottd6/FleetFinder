ALTER TABLE group_listing
    DROP COLUMN is_deleted,
    DROP COLUMN deleted_at,
    DROP COLUMN deleted_by;