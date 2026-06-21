ALTER TABLE conversation
    ADD COLUMN listing_id BIGINT NULL,
    ADD CONSTRAINT fk_conv_references_group_listing
        FOREIGN KEY (listing_id) REFERENCES group_listing (id_group)
            ON DELETE CASCADE;