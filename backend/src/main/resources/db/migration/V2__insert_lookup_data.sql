-- flyway configuration

-- gameplay_category insert
INSERT INTO gameplay_category (category_name)
VALUES
	('Ship Combat'), 
    ('FPS Combat'), 
    ('Piracy'), 
    ('Cargo/Trade'), 
    ('Mining'), 
    ('Salvage'), 
    ('Mission'),
    ('Exploration'),
    ('Medical'),
    ('Racing'),
    ('Event'),
    ('Other');
    
-- gameplay_sub_category insert
INSERT INTO gameplay_subcategory (subcategory_name, category_id)
VALUES
	('Bounty Hunting PVP', (SELECT category_id FROM gameplay_category WHERE category_name = 'Ship Combat')),
    ('Dueling', (SELECT category_id FROM gameplay_category WHERE category_name = 'Ship Combat')),
    ('Bounty Hunting PVE', (SELECT category_id FROM gameplay_category WHERE category_name = 'Ship Combat')),
    ('Other - Ship Combat', (SELECT category_id FROM gameplay_category WHERE category_name = 'Ship Combat')),
    ('Combat Missions', (SELECT category_id FROM gameplay_category WHERE category_name = 'FPS Combat')),
    ('Bunkers', (SELECT category_id FROM gameplay_category WHERE category_name = 'FPS Combat')),
    ('Raid', (SELECT category_id FROM gameplay_category WHERE category_name = 'FPS Combat')),
    ('Other - FPS Combat', (SELECT category_id FROM gameplay_category WHERE category_name = 'FPS Combat')),
    ('Cargo Piracy', (SELECT category_id FROM gameplay_category WHERE category_name = 'Piracy')),
    ('Extortion', (SELECT category_id FROM gameplay_category WHERE category_name = 'Piracy')),
    ('General Marauding', (SELECT category_id FROM gameplay_category WHERE category_name = 'Piracy')),
    ('Other - Piracy', (SELECT category_id FROM gameplay_category WHERE category_name = 'Piracy')),
    ('Cargo Hauling', (SELECT category_id FROM gameplay_category WHERE category_name = 'Cargo/Trade')),
    ('Drug Smuggling', (SELECT category_id FROM gameplay_category WHERE category_name = 'Cargo/Trade')),
    ('Other - Cargo', (SELECT category_id FROM gameplay_category WHERE category_name = 'Cargo/Trade')),
    ('Ship Mining', (SELECT category_id FROM gameplay_category WHERE category_name = 'Mining')),
    ('Ground Vehicle Mining', (SELECT category_id FROM gameplay_category WHERE category_name = 'Mining')),
    ('FPS Mining', (SELECT category_id FROM gameplay_category WHERE category_name = 'Mining')),
    ('Other - Mining', (SELECT category_id FROM gameplay_category WHERE category_name = 'Mining')),
    ('Ship Operated Salvage', (SELECT category_id FROM gameplay_category WHERE category_name = 'Salvage')),
    ('FPS Salvage', (SELECT category_id FROM gameplay_category WHERE category_name = 'Salvage')),
    ('Other - Salvage', (SELECT category_id FROM gameplay_category WHERE category_name = 'Salvage')),
    ('Delivery', (SELECT category_id FROM gameplay_category WHERE category_name = 'Mission')),
    ('Search', (SELECT category_id FROM gameplay_category WHERE category_name = 'Mission')),
    ('Investigation', (SELECT category_id FROM gameplay_category WHERE category_name = 'Mission')),
    ('Mercenary', (SELECT category_id FROM gameplay_category WHERE category_name = 'Mission')),
    ('Maintenance', (SELECT category_id FROM gameplay_category WHERE category_name = 'Mission')),
    ('Bounty Hunting', (SELECT category_id FROM gameplay_category WHERE category_name = 'Mission')),
    ('ECN Alerts', (SELECT category_id FROM gameplay_category WHERE category_name = 'Mission')),
    ('Other - Mission', (SELECT category_id FROM gameplay_category WHERE category_name = 'Mission')),
    ('Prospecting', (SELECT category_id FROM gameplay_category WHERE category_name = 'Exploration')),
    ('Service', (SELECT category_id FROM gameplay_category WHERE category_name = 'Exploration')),
    ('Other - Exploration', (SELECT category_id FROM gameplay_category WHERE category_name = 'Exploration')),
    ('For Hire', (SELECT category_id FROM gameplay_category WHERE category_name = 'Medical')),
    ('Beacon Response', (SELECT category_id FROM gameplay_category WHERE category_name = 'Medical')),
    ('Other - Medical', (SELECT category_id FROM gameplay_category WHERE category_name = 'Medical')),
    ('Organized', (SELECT category_id FROM gameplay_category WHERE category_name = 'Racing')),
    ('Casual', (SELECT category_id FROM gameplay_category WHERE category_name = 'Racing')),
    ('Other - Racing', (SELECT category_id FROM gameplay_category WHERE category_name = 'Racing')),
    ('Current Event', (SELECT category_id FROM gameplay_category WHERE category_name = 'Event')),
    ('Other - Event', (SELECT category_id FROM gameplay_category WHERE category_name = 'Event'));
    
    -- Inserting available planetary systems
INSERT INTO planetary_system (system_name)
VALUES
	('Stanton'),
    ('Pyro'),
    ('Any');
    
-- Inserting planet/moon systems
INSERT INTO planet_moon_system (planet_name, system_id)
VALUES
	('Hurston: Stanton I', (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton')),
    ('Crusader: Stanton II', (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton')),
    ('ArcCorp: Stanton III', (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton')),
    ('MicroTech: Stanton IV', (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton')),
    ('Other - Stanton', (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton')),
    ('Pyro I', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro')),
    ('Monox: Pyro II', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro')),
    ('Bloom: Pyro III', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro')),
    ('Pyro IV', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro')),
    ('Pyro V', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro')),
    ('Terminus: Pyro VI', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro')),
    ('Other - Pyro', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro'));
    
-- Inserting available server regions
INSERT INTO server_region (server_name)
VALUES
	('USA'),
    ('EU'),
    ('AUS'),
    ('Asia'),
    ('Any');
    
-- Inserting available game environments
INSERT INTO game_environment (environment_type)
VALUES
	('LIVE'),
    ('PTU'),
    ('Tech Preview'),
    ('Other/Any');
    
-- Inserting available game experiences
INSERT INTO game_experience (experience_type)
VALUES
	('Persistent Universe'),
    ('Arena Commander');
    
-- Inserting suggested play style options
INSERT INTO play_style (play_style)
VALUES
	('Casual'),
    ('Profit'),
    ('Progression'),
    ('Competitive'),
    ('Skill Development'),
    ('Social'),
    ('Roleplay'),
    ('For Hire'),
    ('LF Service'),
    ('Feature Testing'),
    ('Other');
    
-- Inserting group status options
INSERT INTO group_status (group_status)
VALUES
	('Current/Live'),
    ('Future/Scheduled');
    
-- Inserting legality options
INSERT INTO legality (legality)
VALUES
	('Lawful'),
    ('Unlawful'),
    ('Undefined');
    
-- Inserting PVP options
INSERT INTO pvp_status (pvp_status)
VALUES
	('PvP'),
    ('PvE'),
    ('PvX');
    
    