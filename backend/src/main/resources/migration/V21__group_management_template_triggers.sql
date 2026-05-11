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