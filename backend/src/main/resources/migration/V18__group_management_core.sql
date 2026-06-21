ALTER TABLE group_listing
    ADD COLUMN rsvp_scheduled      TIMESTAMP                                   NULL,
    ADD COLUMN comms_share         VARCHAR(512)                                NULL,
    ADD COLUMN send_comms_share_on ENUM ('JOIN', 'RSVP_CONFIRMED')             NULL,
    ADD COLUMN join_request_prompt VARCHAR(512)                                NULL,
    ADD COLUMN member_banner_msg   VARCHAR(512)                                NULL,
    ADD COLUMN discovery           ENUM ('PUBLIC', 'PRIVATE_LINK', 'CHANNELS') NOT NULL DEFAULT 'PUBLIC',
    ADD COLUMN private_link_uuid   CHAR(36)                                    NULL;

ALTER TABLE listing_template
    ADD COLUMN join_request_prompt VARCHAR(512)                               NULL,
    ADD COLUMN discovery           ENUM ('PUBLIC', 'PRIVATE_LINK', 'CHANNELS') NULL;

ALTER TABLE listing_archive
    ADD COLUMN rsvp_scheduled      TIMESTAMP                                   NULL,
    ADD COLUMN send_comms_share_on ENUM ('JOIN', 'RSVP_CONFIRMED')             NULL,
    ADD COLUMN join_request_prompt VARCHAR(512)                                NULL,
    ADD COLUMN member_banner_msg   VARCHAR(512)                                NULL,
    ADD COLUMN discovery           ENUM ('PUBLIC', 'PRIVATE_LINK', 'CHANNELS') NULL;

ALTER TABLE notification
    MODIFY COLUMN target_metadata JSON NULL;

ALTER TABLE users
    ADD COLUMN in_game_username VARCHAR(32);

UPDATE users
SET in_game_username = user_name
WHERE in_game_username IS NULL;

ALTER TABLE users
    MODIFY COLUMN in_game_username VARCHAR(32) NOT NULL;

# listing_id here refers to group_listing -> id_group to reduce confusion about 'group' labels
CREATE TABLE IF NOT EXISTS group_management_subgroup
(
    id_subgroup            BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    listing_id             BIGINT       NOT NULL,
    root_subgroup_id       BIGINT       NULL,
    parent_subgroup_id     BIGINT       NULL,
    subgroup_label         VARCHAR(64)  NULL,
    subgroup_notes         VARCHAR(255) NULL,
    sort_order             TINYINT      NULL,
    drop_list_orientation  ENUM ('horizontal', 'vertical', 'mixed') NOT NULL DEFAULT 'vertical',
    deleted_at             TIMESTAMP    NULL,
    created_at             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_subgroup_references_group_listing
        FOREIGN KEY (listing_id) REFERENCES group_listing (id_group)
            ON DELETE CASCADE,

    CONSTRAINT fk_subgroup_references_root_subgroup
        FOREIGN KEY (root_subgroup_id) REFERENCES group_management_subgroup (id_subgroup)
            ON DELETE CASCADE,

    CONSTRAINT fk_subgroup_references_parent_subgroup
        FOREIGN KEY (parent_subgroup_id) REFERENCES group_management_subgroup (id_subgroup)
            ON DELETE SET NULL
);

# this is the actual class for the rank, it references assigned privilege to
# define what privileges the rank has
CREATE TABLE IF NOT EXISTS in_group_rank
(
    id_rank         BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    listing_id      BIGINT      NULL,
    is_scoped       TINYINT     NOT NULL DEFAULT 0,
    rank_scope_id   BIGINT      NULL,
    rank_title      VARCHAR(32) NOT NULL,
    rank_notes      VARCHAR(64) NULL,
    is_default_rank TINYINT     NOT NULL DEFAULT 0,
    created_by_id   BIGINT      NULL,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_in_grp_rank_references_listing_id
        FOREIGN KEY (listing_id) REFERENCES group_listing (id_group)
            ON DELETE CASCADE,

    CONSTRAINT fk_in_grp_rank_references_creator
        FOREIGN KEY (created_by_id) REFERENCES users (id_user)
            ON DELETE SET NULL,

    CONSTRAINT fk_in_group_rank_scope_references_grp_mgmt_subgroup
        FOREIGN KEY (rank_scope_id) REFERENCES group_management_subgroup (id_subgroup)
            ON DELETE CASCADE
);


# reference table for privilege actions/types
CREATE TABLE IF NOT EXISTS rank_privilege_type
(
    id_privilege VARCHAR(32) NOT NULL PRIMARY KEY
);

# privilege actions/types assigned to a rank
CREATE TABLE IF NOT EXISTS group_rank_assigned_privilege
(
    rank_id      BIGINT      NOT NULL,
    privilege_id VARCHAR(32) NOT NULL,

    CONSTRAINT assigned_priv_primary_key
        PRIMARY KEY (rank_id, privilege_id),

    CONSTRAINT fk_assigned_priv_references_rank_id
        FOREIGN KEY (rank_id) REFERENCES in_group_rank (id_rank)
            ON DELETE CASCADE,

    CONSTRAINT fk_assigned_privilege_references_rank_priv_type
        FOREIGN KEY (privilege_id) REFERENCES rank_privilege_type (id_privilege)
            ON DELETE CASCADE
);

# members who have accepted an invite to the group
CREATE TABLE IF NOT EXISTS group_member
(
    listing_id       BIGINT       NOT NULL,
    user_id          BIGINT       NOT NULL,
    member_status    ENUM ('ACTIVE', 'WAITLIST') NOT NULL DEFAULT 'ACTIVE',
    in_group_rank_id BIGINT       NULL,
    has_mic          TINYINT      NULL,
    has_headset      TINYINT      NULL,
    member_note      VARCHAR(255) NULL,
    has_ext_notes    TINYINT      NOT NULL DEFAULT 0,
    rsvp_status      ENUM ('PENDING', 'CONFIRMED', 'DECLINED'),
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_group_member
        PRIMARY KEY (listing_id, user_id),

    CONSTRAINT fk_grp_member_references_in_group_rank
        FOREIGN KEY (in_group_rank_id) REFERENCES in_group_rank (id_rank)
            ON DELETE SET NULL,

    CONSTRAINT fk_member_references_listing
        FOREIGN KEY (listing_id) REFERENCES group_listing (id_group)
            ON DELETE CASCADE,

    CONSTRAINT fk_member_references_user
        FOREIGN KEY (user_id) REFERENCES users (id_user)
            ON DELETE CASCADE
);

# labels for crew roles, can be associated with a user or NULL and accessible to all
CREATE TABLE IF NOT EXISTS crew_role_classification
(
    id_role         BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    role_category   VARCHAR(32) NOT NULL,
    role_title      VARCHAR(32) NOT NULL, #uq1
    creator_id      BIGINT      NULL,     #uq1
    is_generic_role TINYINT     NOT NULL DEFAULT 0,

    CONSTRAINT fk_role_type_references_user
        FOREIGN KEY (creator_id) REFERENCES users (id_user)
            ON DELETE CASCADE,

    CONSTRAINT uq_role_type_for_role_title_and_user
        UNIQUE KEY (role_title, creator_id)
);

# crew slot within a subgroup
CREATE TABLE IF NOT EXISTS mgmt_crew_position
(
    id_position        BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    listing_id         BIGINT       NOT NULL, #ref #uq1
    root_subgroup_id   BIGINT       NULL,
    subgroup_id        BIGINT       NOT NULL, #ref
    sort_order         TINYINT      NULL,
    position_role_id   BIGINT       NULL, #ref
    position_note      VARCHAR(128) NULL,
    assigned_member_id BIGINT       NULL,     #ref #uq1
    deleted_at         TIMESTAMP    NULL,
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_crew_position_references_listing
        FOREIGN KEY (listing_id) REFERENCES group_listing (id_group)
            ON DELETE CASCADE,

    CONSTRAINT fk_crew_position_references_root_subgroup
        FOREIGN KEY (root_subgroup_id) REFERENCES group_management_subgroup (id_subgroup)
            ON DELETE CASCADE,

    CONSTRAINT fk_crew_position_references_mgmt_subgroup
        FOREIGN KEY (subgroup_id) REFERENCES group_management_subgroup (id_subgroup)
            ON DELETE CASCADE,

    CONSTRAINT fk_crew_position_references_crew_role
        FOREIGN KEY (position_role_id) REFERENCES crew_role_classification (id_role)
            ON DELETE SET NULL,

    # this must be handled in app so that whenever a member entity is deleted, it removes their position first
    CONSTRAINT fk_crew_position_may_reference_group_member
        FOREIGN KEY (listing_id, assigned_member_id) REFERENCES group_member (listing_id, user_id)
            ON DELETE CASCADE,

    CONSTRAINT uq_group_member_to_crew_position
        UNIQUE KEY (listing_id, assigned_member_id)
);

CREATE TABLE IF NOT EXISTS group_invite
(
    id_invite           BIGINT                                                                   NOT NULL PRIMARY KEY AUTO_INCREMENT,
    listing_id          BIGINT                                                                   NOT NULL, #ref
    sender_id           BIGINT                                                                   NOT NULL, #ref
    recipient_id        BIGINT                                                                   NOT NULL, #ref
    direction           ENUM ('OFFER', 'REQUEST')                                                NOT NULL,
    roster_class        ENUM ('ACTIVE', 'WAITLIST')                                              NOT NULL,
    role_id             BIGINT                                                                   NULL,
    invite_status       ENUM ('PENDING', 'ACCEPTED', 'DECLINED', 'RESCINDED', 'LEFT_OR_REMOVED') NOT NULL DEFAULT 'PENDING',
    invite_message      VARCHAR(255)                                                             NULL,
    has_mic             TINYINT                                                                  NULL,
    has_headset         TINYINT                                                                  NULL,
    active              TINYINT                                                                  NULL     DEFAULT 1,
    sender_dismissed    TINYINT                                                                  NOT NULL DEFAULT 0,
    recipient_dismissed TINYINT                                                                  NOT NULL DEFAULT 0,
    expires_at          TIMESTAMP                                                                NULL,
    created_at          TIMESTAMP                                                                NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_group_invite_references_group_listing
        FOREIGN KEY (listing_id) REFERENCES group_listing (id_group)
            ON DELETE CASCADE,

    CONSTRAINT fk_group_invite_references_sender
        FOREIGN KEY (sender_id) REFERENCES users (id_user)
            ON DELETE CASCADE,

    CONSTRAINT fk_group_invite_references_recipient
        FOREIGN KEY (recipient_id) REFERENCES users (id_user)
            ON DELETE CASCADE,

    CONSTRAINT fk_group_invite_references_role_classification
        FOREIGN KEY (role_id) REFERENCES crew_role_classification (id_role)
            ON DELETE SET NULL,

    CONSTRAINT valid_active_value_check CHECK (active is NULL or active = 1),

    CONSTRAINT uq_group_invite_on_direction_users_listing_and_active
        UNIQUE KEY (direction, sender_id, recipient_id, listing_id, active)
);