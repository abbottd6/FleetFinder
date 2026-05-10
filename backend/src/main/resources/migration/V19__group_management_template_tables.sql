CREATE TABLE IF NOT EXISTS crew_template
(
    id_template       BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    template_label    VARCHAR(64) NOT NULL,
    template_category ENUM ('Capital', 'Large', 'Medium', 'Small', 'Custom'),
    owner_id          BIGINT      NULL,
    last_used_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_crew_template_references_user
        FOREIGN KEY (owner_id) REFERENCES users (id_user),

    CONSTRAINT uq_user_template_label
        UNIQUE KEY (owner_id, template_label)
);

CREATE TABLE IF NOT EXISTS crew_subgroup_template
(
    id_template_subgroup   BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    template_id            BIGINT       NOT NULL, #ref
    parent_subgroup_id     BIGINT       NULL,     #ref
    subgroup_label         VARCHAR(64)  NOT NULL,
    subgroup_notes         VARCHAR(255) NULL,
    sort_order             TINYINT      NOT NULL DEFAULT 0,
    intended_subgroup_size TINYINT      NULL,

    CONSTRAINT fk_template_subgroup_references_template
        FOREIGN KEY (template_id) REFERENCES crew_template (id_template)
            ON DELETE CASCADE,
    CONSTRAINT fk_template_subgroup_references_parent_subgroup
        FOREIGN KEY (parent_subgroup_id) REFERENCES crew_subgroup_template (id_template_subgroup)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS crew_position_template
(
    id_template_position BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    root_template_id     BIGINT       NOT NULL, #ref
    subgroup_template_id BIGINT       NOT NULL, #ref
    position_role_id     BIGINT       NULL,     #ref
    position_notes       VARCHAR(255) NULL,
    sort_order           TINYINT      NOT NULL DEFAULT 0,

    CONSTRAINT fk_position_template_references_template_root
        FOREIGN KEY (root_template_id) REFERENCES crew_template (id_template),

    CONSTRAINT fk_position_template_references_subgroup_template
        FOREIGN KEY (subgroup_template_id) REFERENCES crew_subgroup_template (id_template_subgroup)
            ON DELETE CASCADE,

    CONSTRAINT fk_position_template_references_crew_role_classification
        FOREIGN KEY (position_role_id) REFERENCES crew_role_classification (id_role)
            ON DELETE SET NULL
)