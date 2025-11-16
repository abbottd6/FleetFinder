ALTER TABLE planetary_system
    ADD COLUMN sort_order INT NOT NULL DEFAULT 0;

UPDATE planetary_system SET sort_order = 999 WHERE system_name = 'Any';