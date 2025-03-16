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


SET @ship_combat_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Ship Combat');
SET @fps_combat_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'FPS Combat');
SET @piracy_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Piracy');
SET @cargo_trade_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Cargo/Trade');
SET @mining_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Mining');
SET @salvage_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Salvage');
SET @mission_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Mission');
SET @exploration_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Exploration');
SET @medical_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Medical');
SET @racing_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Racing');
SET @event_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Event');

-- gameplay_sub_category insert
INSERT INTO gameplay_subcategory (subcategory_name, category_id)
VALUES
	('Bounty Hunting PVP', @ship_combat_id),
    ('Dueling', @ship_combat_id),
    ('Bounty Hunting PVE', @ship_combat_id),
    ('Other - Ship Combat', @ship_combat_id),
    ('Combat Missions', @fps_combat_id),
    ('Bunkers', @fps_combat_id),
    ('Raid', @fps_combat_id),
    ('Other - FPS Combat', @fps_combat_id),
    ('Cargo Piracy', @piracy_id),
    ('Extortion', @piracy_id),
    ('General Marauding', @piracy_id),
    ('Other - Piracy', @piracy_id),
    ('Cargo Hauling', @cargo_trade_id),
    ('Drug Smuggling', @cargo_trade_id),
    ('Other - Cargo', @cargo_trade_id),
    ('Ship Mining', @mining_id),
    ('Ground Vehicle Mining', @mining_id),
    ('FPS Mining', @mining_id),
    ('Other - Mining', @mining_id),
    ('Ship Operated Salvage', @salvage_id),
    ('FPS Salvage', @salvage_id),
    ('Other - Salvage', @salvage_id),
    ('Delivery', @mission_id),
    ('Search', @mission_id),
    ('Investigation', @mission_id),
    ('Mercenary', @mission_id),
    ('Maintenance', @mission_id),
    ('Bounty Hunting', @mission_id),
    ('ECN Alerts', @mission_id),
    ('Other - Mission', @mission_id),
    ('Prospecting', @exploration_id),
    ('Service', @exploration_id),
    ('Other - Exploration', @exploration_id),
    ('For Hire', @medical_id),
    ('Beacon Response', @medical_id),
    ('Other - Medical', @medical_id),
    ('Organized', @racing_id),
    ('Casual', @racing_id),
    ('Other - Racing', @racing_id),
    ('Current Event', @event_id),
    ('Other - Event', @event_id);
    
    -- Inserting available planetary systems
INSERT INTO planetary_system (system_name)
VALUES
	('Stanton'),
    ('Pyro'),
    ('Any');

SET @stanton_id = (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton');
SET @pyro_id = (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro');
    
-- Inserting planet/moon systems
INSERT INTO planet_moon_system (planet_name, system_id)
VALUES
	('Hurston: Stanton I', @stanton_id),
    ('Crusader: Stanton II', @stanton_id),
    ('ArcCorp: Stanton III', @stanton_id),
    ('MicroTech: Stanton IV', @stanton_id),
    ('Other - Stanton', @stanton_id),
    ('Pyro I', @pyro_id),
    ('Monox: Pyro II', @pyro_id),
    ('Bloom: Pyro III', @pyro_id),
    ('Pyro IV', @pyro_id),
    ('Pyro V', @pyro_id),
    ('Terminus: Pyro VI', @pyro_id),
    ('Other - Pyro', @pyro_id);
    
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
    
    