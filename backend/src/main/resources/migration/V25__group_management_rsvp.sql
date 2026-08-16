CREATE TABLE IF NOT EXISTS rsvp_master
(
    id_rsvp_master BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    listing_id     BIGINT       NOT NULL,
    subgroup_id    BIGINT       NULL,
    scheduled_ts   TIMESTAMP    NOT NULL,
    rsvp_message   VARCHAR(255) NULL,
    comms_share    VARCHAR(255) NULL,
    batch_id       BINARY(16)   NULL,
    expires_at     TIMESTAMP    NOT NULL,
    created_at     TIMESTAMP    NOT NULL,

    CONSTRAINT fk_rsvp_master_ref_group_listing
        FOREIGN KEY (listing_id) REFERENCES group_listing (id_group),

    CONSTRAINT fk_rsvp_master_ref_subgroup
        FOREIGN KEY (subgroup_id) REFERENCES group_management_subgroup (id_subgroup),

    CONSTRAINT uq_rsvp_master_listing_and_subgroup
        UNIQUE KEY (listing_id, subgroup_id)
);

CREATE TABLE IF NOT EXISTS rsvp_member
(
    id_rsvp_member BIGINT                                              NOT NULL PRIMARY KEY AUTO_INCREMENT,
    rsvp_master_id BIGINT                                              NOT NULL,
    listing_id     BIGINT                                              NOT NULL,
    position_id    BIGINT                                              NULL,
    user_id        BIGINT                                              NOT NULL,
    rsvp_message   VARCHAR(255),
    status         ENUM ('PENDING', 'ACCEPTED', 'DECLINED', 'EXPIRED') NOT NULL DEFAULT 'PENDING',
    batch_id       BINARY(16)                                          NOT NULL,
    expires_at     TIMESTAMP                                           NOT NULL,
    created_at     TIMESTAMP                                           NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_rsvp_member_ref_rsvp_master
        FOREIGN KEY (rsvp_master_id) REFERENCES rsvp_master (id_rsvp_master),

    CONSTRAINT fk_rsvp_member_ref_group_listing
        FOREIGN KEY (listing_id) REFERENCES group_listing (id_group),

    CONSTRAINT fk_rsvp_member_ref_position
        FOREIGN KEY (position_id) REFERENCES mgmt_crew_position (id_position),

    CONSTRAINT fk_rsvp_member_ref_user
        FOREIGN KEY (user_id) REFERENCES users (id_user),

    CONSTRAINT uq_rsvp_member_user_and_master
        UNIQUE KEY (rsvp_master_id, user_id)
);