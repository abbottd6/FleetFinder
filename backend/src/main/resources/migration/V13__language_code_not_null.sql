UPDATE group_listing SET language_code = 'English' WHERE language_code IS NULL;
UPDATE listing_archive SET language_code = 'English' WHERE language_code IS NULL;
UPDATE listing_template SET language_code = 'English' WHERE language_code IS NULL;

ALTER TABLE group_listing MODIFY COLUMN language_code VARCHAR(10) NOT NULL;
ALTER TABLE listing_archive MODIFY COLUMN language_code VARCHAR(10) NOT NULL;
ALTER TABLE listing_template MODIFY COLUMN language_code VARCHAR(10) NOT NULL;
