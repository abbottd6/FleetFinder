ALTER TABLE group_member
    ADD CONSTRAINT fk_member_references_listing
        FOREIGN KEY (listing_id) REFERENCES group_listing (id_group)
        ON DELETE CASCADE,
    ADD CONSTRAINT fk_member_references_user
        FOREIGN KEY (user_id) REFERENCES users (id_user)
        ON DELETE CASCADE;