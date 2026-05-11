CREATE TRIGGER trg_calc_group_subgroup_sort_order
    BEFORE INSERT ON group_management_subgroup
    FOR EACH ROW
    SET NEW.sort_order = IF(
        NEW.sort_order IS NULL OR NEW.sort_order = 0,
        (SELECT COALESCE(MAX(sort_order), 0) + 1
         FROM group_management_subgroup
         WHERE parent_subgroup_id <=> NEW.parent_subgroup_id
            AND listing_id = NEW.listing_id),
        NEW.sort_order
    );

CREATE TRIGGER trg_calc_crew_position_sort_order
    BEFORE INSERT ON mgmt_crew_position
    FOR EACH ROW
    SET NEW.sort_order = IF(
        NEW.sort_order IS NULL OR NEW.sort_order = 0,
        (SELECT COALESCE(MAX(sort_order), 0) + 1
         FROM mgmt_crew_position
         WHERE subgroup_id = NEW.subgroup_id
            AND listing_id = NEW.listing_id),
        NEW.sort_order
    );

CREATE TRIGGER trg_set_group_member_default_rank_on_insert
    BEFORE INSERT ON group_member
    FOR EACH ROW
    SET NEW.in_group_rank_id = IF(
        NEW.in_group_rank_id IS NULL,
        (SELECT id_rank
         FROM in_group_rank
         WHERE is_default_rank = 1 AND rank_title = 'Member'
         LIMIT 1),
        NEW.in_group_rank_id
    );

CREATE TRIGGER trg_update_member_rank_on_subgroup_delete
    BEFORE DELETE ON group_management_subgroup
    FOR EACH ROW
    UPDATE group_member member
    SET member.in_group_rank_id = (
        SELECT id_rank
        FROM in_group_rank
        WHERE is_default_rank = 1
            AND rank_title = 'Member'
        LIMIT 1
    )
    WHERE member.in_group_rank_id IN (
        SELECT id_rank FROM in_group_rank
        WHERE rank_scope_id = OLD.id_subgroup
    );

CREATE TRIGGER trg_update_member_rank_on_rank_delete
    BEFORE DELETE ON in_group_rank
    FOR EACH ROW
    UPDATE group_member member
    SET member.in_group_rank_id = (
        SELECT id_rank
        FROM in_group_rank
        WHERE is_default_rank = 1
            AND rank_title = 'Member'
        LIMIT 1
    )
    WHERE member.in_group_rank_id = OLD.id_rank;

CREATE TRIGGER trg_prevent_default_rank_delete
    BEFORE DELETE ON in_group_rank
    FOR EACH ROW
    BEGIN
        IF OLD.is_default_rank = 1 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot delete a default rank.';
        END IF;
    END;

CREATE TRIGGER trg_prevent_default_role_class_delete
    BEFORE DELETE ON crew_role_classification
    FOR EACH ROW
    BEGIN
        IF OLD.is_generic_role = 1 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot delete generic roles.';
        END IF;
    END;


