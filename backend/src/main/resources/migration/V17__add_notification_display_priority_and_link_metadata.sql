ALTER TABLE notification
    ADD COLUMN dropdown_priority TINYINT(1) NOT NULL DEFAULT 1,
    ADD COLUMN target_metadata JSON NULL;