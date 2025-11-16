INSERT INTO planetary_system (system_name)
VALUES ('Nyx');

SET @nyx_id = (SELECT system_id FROM planetary_system WHERE system_name = 'Nyx');

INSERT INTO planet_moon_system (planet_name, system_id)
VALUES
    ('Nyx I', @nyx_id),
    ('Nyx II', @nyx_id),
    ('Nyx III', @nyx_id),
    ('Delamar', @nyx_id);