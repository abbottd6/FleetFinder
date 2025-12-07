CREATE TABLE hidden_listing
(
    hide_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_user BIGINT NOT NULL,
    id_group BIGINT NOT NULL,
    hidden_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT hidden_listing_fk_ref_user
        FOREIGN KEY (id_user)
            REFERENCES users (id_user)
            ON DELETE CASCADE,
    CONSTRAINT hidden_listing_fk_ref_group
        FOREIGN KEY (id_group)
            REFERENCES group_listing (id_group)
            ON DELETE CASCADE,
    CONSTRAINT uq_user_and_listing
        UNIQUE (id_user, id_group)
);