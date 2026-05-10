CREATE TRIGGER trg_calc_crew_subgroup_template_sort_order
    BEFORE INSERT ON crew_subgroup_template
    FOR EACH ROW
    SET NEW.sort_order = IF(
        NEW.sort_order IS NULL OR NEW.sort_order = 0,
        (SELECT COALESCE(MAX(sort_order), 0) + 1
         FROM crew_subgroup_template
         WHERE parent_subgroup_id <=> NEW.parent_subgroup_id),
        NEW.sort_order
    );

CREATE TRIGGER trg_calc_crew_position_template_sort_order
    BEFORE INSERT ON crew_position_template
    FOR EACH ROW
    SET NEW.sort_order = IF(
        NEW.sort_order IS NULL OR NEW.sort_order = 0,
        (SELECT COALESCE(MAX(sort_order), 0) + 1
         FROM crew_position_template
         WHERE subgroup_template_id = NEW.subgroup_template_id),
        NEW.sort_order
    );

CREATE TRIGGER trg_prevent_generic_template_delete
    BEFORE DELETE ON crew_template
    FOR EACH ROW
    BEGIN
        IF OLD.owner_id IS NULL
            OR OLD.template_category <> 'Custom' THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot delete generic templates.';
        END IF;
    END;