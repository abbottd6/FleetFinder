ALTER TABLE group_invite
    DROP INDEX uq_group_invite_type_sender_recipient_group,
    ADD COLUMN active TINYINT NULL DEFAULT 1,
    ADD COLUMN sender_dismissed TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN recipient_dismissed TINYINT NOT NULL DEFAULT 0,
    ADD CONSTRAINT valid_active_value_check CHECK (active is NULL or active = 1),
    ADD CONSTRAINT uq_group_invite_on_direction_users_listing_and_active
        UNIQUE KEY (direction, sender_id, recipient_id, listing_id, active);