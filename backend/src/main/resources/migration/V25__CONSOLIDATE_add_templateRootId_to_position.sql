ALTER TABLE crew_position_template
    ADD COLUMN template_id BIGINT;

UPDATE crew_position_template p
JOIN crew_subgroup_template s ON p.subgroup_template_id = s.id_template_subgroup
SET p.template_id = s.template_id;

ALTER TABLE crew_position_template
    MODIFY COLUMN template_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_position_template_references_template_root
        FOREIGN KEY (template_id) REFERENCES crew_template (id_template);