ALTER TABLE group_listing
    ADD COLUMN rsvp_required TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN rsvp_scheduled TIMESTAMP NULL;

ALTER TABLE conversation
    ADD COLUMN listing_id BIGINT NULL;

# listing_id here refers to group_listing -> id_group to reduce confusion about 'group' labels
CREATE TABLE IF NOT EXISTS group_management_subgroup (
    id_subgroup BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    listing_id BIGINT NOT NULL,
    parent_subgroup_id BIGINT NULL,
    subgroup_label VARCHAR(64) NULL,
    subgroup_notes VARCHAR(255) NULL,
    intended_subgroup_size TINYINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_subgroup_references_group_listing
        FOREIGN KEY (listing_id) REFERENCES group_listing (id_group)
            ON DELETE CASCADE,
    CONSTRAINT fk_subgroup_references_parent_subgroup
        FOREIGN KEY (parent_subgroup_id) REFERENCES group_management_subgroup (id_subgroup)
);

# this is the actual class for the rank, it references assigned privilege to
# define what privileges the rank has
CREATE TABLE IF NOT EXISTS in_group_rank (
    id_rank BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    listing_id BIGINT NULL,
    rank_scope_id BIGINT NULL,
    rank_title VARCHAR(32) NULL,
    rank_notes VARCHAR(64) NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_in_grp_rank_references_listing_id
        FOREIGN KEY (listing_id) REFERENCES group_listing (id_group)
            ON DELETE CASCADE,

    CONSTRAINT fk_in_group_rank_scope_references_grp_mgmt_subgrp
        FOREIGN KEY (rank_scope_id) REFERENCES group_management_subgroup (id_subgroup)
            ON DELETE CASCADE
);


# reference table for privilege actions/types
CREATE TABLE IF NOT EXISTS rank_privilege_type (
    id_privilege VARCHAR(32) NOT NULL PRIMARY KEY
);

# privilege actions/types assigned to a rank
CREATE TABLE IF NOT EXISTS group_rank_assigned_privilege (
    id_rank BIGINT NOT NULL,
    id_privilege VARCHAR(32) NOT NULL,

    CONSTRAINT assigned_priv_primary_key
        PRIMARY KEY (id_rank, id_privilege),

    CONSTRAINT fk_assigned_priv_references_rank_id
        FOREIGN KEY (id_rank) REFERENCES in_group_rank (id_rank),

    CONSTRAINT fk_assinged_privilege_references_rank_priv_type
        FOREIGN KEY (id_privilege) REFERENCES rank_privilege_type (id_privilege)
);

CREATE TABLE IF NOT EXISTS group_member (
    listing_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    member_status ENUM('ACTIVE', 'WAITLIST'),
    subgroup_id BIGINT NULL,
    in_group_rank_id BIGINT NULL,
    has_comms TINYINT NOT NULL DEFAULT 0,
    member_note VARCHAR(255) NULL,
    has_ext_notes TINYINT NOT NULL DEFAULT 0,
    rsvp_status ENUM('PENDING', 'CONFIRMED', 'DECLINED'),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_group_member
        PRIMARY KEY (listing_id, user_id),

    CONSTRAINT fk_grp_member_references_subgroup
        FOREIGN KEY (subgroup_id) REFERENCES group_management_subgroup (id_subgroup),

    CONSTRAINT fk_grp_member_references_in_group_rank
        FOREIGN KEY (in_group_rank_id) REFERENCES in_group_rank (id_rank)
);

CREATE TABLE IF NOT EXISTS subgroup_role (
    id_role
)