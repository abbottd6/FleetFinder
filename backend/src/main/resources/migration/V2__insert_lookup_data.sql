-- flyway configuration

-- gameplay_category insert
INSERT INTO gameplay_category (category_name)
VALUES
    ('Commerce/Trade'),
    ('Event'),
    ('Exploration'),
    ('FPS Combat'),
    ('Hauling/Freight'),
    ('Medical'),
    ('Mining'),
    ('Piracy'),
    ('Racing'),
    ('Salvage'),
    ('Ship Combat'),
    ('Other');

SET @commerce_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Commerce/Trade');
SET @ship_combat_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Ship Combat');
SET @fps_combat_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'FPS Combat');
SET @piracy_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Piracy');
SET @hauling_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Hauling/Freight');
SET @mining_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Mining');
SET @salvage_id = (SELECT category_id FROM gameplay_category WHERE category_name = 'Salvage');
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
    ('Escort/Security', @ship_combat_id),
    ('Other - Ship Combat', @ship_combat_id),
    ('FPS Combat Contract', @fps_combat_id),
    ('Contested Zone', @fps_combat_id),
    ('Bunker Raid', @fps_combat_id),
    ('Event Raid', @fps_combat_id),
    ('Other - FPS Combat', @fps_combat_id),
    ('Interdiction/Snare', @piracy_id),
    ('Boarding/Soft-Death', @piracy_id),
    ('General Marauding', @piracy_id),
    ('Other - Piracy', @piracy_id),
    ('Contract Hauling', @hauling_id),
    ('Risk Hauling', @hauling_id),
    ('Smuggling', @hauling_id),
    ('Courier/Small Freight', @hauling_id),
    ('Other - Hauling', @hauling_id),
    ('Commodity Trading', @commerce_id),
    ('Market Runs/Arbitrage', @commerce_id),
    ('Interstellar Commerce', @commerce_id),
    ('Ship Mining', @mining_id),
    ('Ground Vehicle Mining', @mining_id),
    ('FPS Mining', @mining_id),
    ('Prospecting', @mining_id),
    ('Refining', @mining_id),
    ('Other - Mining', @mining_id),
    ('Hull Scraping', @salvage_id),
    ('Structural Salvage', @salvage_id),
    ('Cargo Recovery', @salvage_id),
    ('Other - Salvage', @salvage_id),
    ('Prospecting', @exploration_id),
    ('POI/Wreck Hunting', @exploration_id),
    ('Touring/Sightseeing', @exploration_id),
    ('Navigation Practice', @exploration_id),
    ('Other - Exploration', @exploration_id),
    ('For Hire', @medical_id),
    ('Beacon Response', @medical_id),
    ('Transport', @medical_id),
    ('Other - Medical', @medical_id),
    ('Organized', @racing_id),
    ('Casual', @racing_id),
    ('Other - Racing', @racing_id),
    ('Current Event', @event_id),
    ('Org Event', @event_id),
    ('Other - Event', @event_id);
    
    -- Inserting available planetary systems
INSERT INTO planetary_system (system_name, sort_order)
VALUES
	('Stanton', 0),
    ('Pyro', 0),
    ('Nyx', 0),
    ('Any', 999);

SET @stanton_id = (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton');
SET @pyro_id = (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro');
SET @nyx_id = (SELECT system_id FROM planetary_system WHERE system_name = 'Nyx');
    
-- Inserting planet/moon systems
INSERT INTO planet_moon_system (planet_name, system_id)
VALUES
	('Hurston: Stanton I', @stanton_id),
    ('Crusader: Stanton II', @stanton_id),
    ('ArcCorp: Stanton III', @stanton_id),
    ('MicroTech: Stanton IV', @stanton_id),
    ('Pyro I', @pyro_id),
    ('Monox: Pyro II', @pyro_id),
    ('Bloom: Pyro III', @pyro_id),
    ('Pyro IV', @pyro_id),
    ('Pyro V', @pyro_id),
    ('Terminus: Pyro VI', @pyro_id),
    ('Nyx I', @nyx_id),
    ('Nyx II', @nyx_id),
    ('Nyx III', @nyx_id),
    ('Delamar', @nyx_id);
    
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
    ('Evocati');
    
-- Inserting available game experiences
INSERT INTO game_experience (experience_type)
VALUES
	('Persistent Universe'),
    ('Arena Commander');
    
-- Inserting suggested play style options
INSERT INTO play_style (play_style)
VALUES
	('Casual'),
    ('Competitive'),
    ('Learning'),
    ('Feature Testing'),
    ('Stream (Watchable)'),
    ('Stream (Joinable)');
    
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
    
    