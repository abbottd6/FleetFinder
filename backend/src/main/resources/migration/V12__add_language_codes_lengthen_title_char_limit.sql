ALTER TABLE group_listing
    MODIFY COLUMN listing_title VARCHAR(128) NOT NULL,
    ADD COLUMN language_code VARCHAR(10);

ALTER TABLE listing_archive
    MODIFY COLUMN action_type ENUM('None', 'AutoMod', 'Manual') NOT NULL DEFAULT 'None',
    MODIFY COLUMN listing_title VARCHAR(128) NOT NULL,
    ADD COLUMN language_code VARCHAR(10);

ALTER TABLE listing_template
    MODIFY COLUMN listing_title VARCHAR(128) NOT NULL,
    ADD COLUMN language_code VARCHAR(10);