ALTER TABLE group_management_subgroup
    ADD COLUMN root_subgroup_id BIGINT NULL,
    ADD CONSTRAINT fk_subgroup_references_root_subgroup
        FOREIGN KEY (root_subgroup_id) REFERENCES group_management_subgroup (id_subgroup)
            ON DELETE CASCADE;

ALTER TABLE mgmt_crew_position
    ADD COLUMN root_subgroup_id BIGINT NULL,
    ADD CONSTRAINT fk_crew_position_references_root_subgroup
        FOREIGN KEY (root_subgroup_id) REFERENCES group_management_subgroup (id_subgroup)
            ON DELETE CASCADE;