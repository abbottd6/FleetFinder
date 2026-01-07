ALTER TABLE group_listing
    ADD COLUMN vis_status VARCHAR(12) NOT NULL DEFAULT 'FRESH',
    ADD CONSTRAINT chk_listing_vis_status
    CHECK (vis_status IN ('FRESH', 'RECENT', 'STALE', 'EXPIRED'));