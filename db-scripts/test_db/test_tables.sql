DROP DATABASE IF EXISTS test_sc_fleetfinder;

CREATE DATABASE test_sc_fleetfinder;
USE test_sc_fleetfinder;

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.`user` (
id_user BIGINT(20) NOT NULL AUTO_INCREMENT,
user_name VARCHAR(32) NOT NULL,
user_password VARCHAR(32) NOT NULL,
email VARCHAR(45) NOT NULL,
server_id INT,
org VARCHAR(25),
about_user VARCHAR(255),
acct_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (id_user)
);

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.gameplay_category (
category_id INT NOT NULL AUTO_INCREMENT,
category_name VARCHAR(25) NOT NULL,
PRIMARY KEY(category_id)
);

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.gameplay_subcategory (
subcategory_id INT NOT NULL AUTO_INCREMENT,
subcategory_name VARCHAR(25) NOT NULL,
category_id INT NOT NULL,
PRIMARY KEY(subcategory_id),
FOREIGN KEY(category_id) REFERENCES gameplay_category(category_id)
);

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.planetary_system (
system_id INT NOT NULL AUTO_INCREMENT,
system_name VARCHAR(25) NOT NULL,
PRIMARY KEY(system_id)
);

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.planet_moon_system (
planet_id INT NOT NULL AUTO_INCREMENT,
planet_name VARCHAR(25) NOT NULL,
system_id INT NOT NULL,
PRIMARY KEY(planet_id),
FOREIGN KEY(system_id) REFERENCES planetary_system(system_id)
);

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.server_region (
server_id INT NOT NULL AUTO_INCREMENT,
server_name VARCHAR(12) NOT NULL,
PRIMARY KEY(server_id)
);

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.game_environment (
environment_id INT NOT NULL AUTO_INCREMENT,
environment_type VARCHAR(16) NOT NULL,
PRIMARY KEY(environment_id)
);

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.game_experience (
experience_id INT NOT NULL AUTO_INCREMENT,
experience_type VARCHAR(20) NOT NULL,
PRIMARY KEY(experience_id)
);

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.play_style (
style_id INT NOT NULL AUTO_INCREMENT,
play_style VARCHAR(12),
PRIMARY KEY(style_id)
);

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.group_status (
group_status_id INT NOT NULL AUTO_INCREMENT,
group_status VARCHAR(6),
PRIMARY KEY(group_status_id)
);

-- Inserting group status options
INSERT INTO test_sc_fleetfinder.group_status (group_status)
VALUES
	('Current/Active'),
    ('Future/Planned');

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.legality (
legality_id INT NOT NULL AUTO_INCREMENT,
legality VARCHAR(9) NOT NULL,
PRIMARY KEY(legality_id)
);

-- Inserting legality options
INSERT INTO test_sc_fleetfinder.legality (legality)
VALUES
	('Lawful'),
    ('Unlawful'),
    ('Undefined');

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.pvp_status (
pvp_status_id INT NOT NULL AUTO_INCREMENT,
pvp_status VARCHAR(3) NOT NULL,
PRIMARY KEY(pvp_status_id)
);

-- Inserting PVP options
INSERT INTO test_sc_fleetfinder.pvp_status (pvp_status)
VALUES
	('PvP'),
    ('PvE'),
    ('PvX');

CREATE TABLE IF NOT EXISTS test_sc_fleetfinder.group_listing (
id_group BIGINT NOT NULL AUTO_INCREMENT,
id_user BIGINT NOT NULL,
server_id INT NOT NULL,
environment_id INT NOT NULL,
experience_id INT NOT NULL,
listing_title VARCHAR(65) NOT NULL,
listing_user VARCHAR(32) NOT NULL,
style_id INT,
legality_id INT NOT NULL,
group_status_id INT NOT NULL,
event_schedule DATETIME NOT NULL,
category_id INT,
subcategory_id INT,
pvp_status_id INT NOT NULL,
system_id INT,
planet_id INT,
activity_description VARCHAR(500) NOT NULL,
desired_party_size TINYINT,
available_roles VARCHAR(255),
comms_options ENUM('REQUIRED', 'OPTIONAL', 'NO COMMS') NOT NULL,
comms_service VARCHAR(25),
last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
creation_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
PRIMARY KEY(id_group),
FOREIGN KEY(id_user) REFERENCES `user`(id_user) ON DELETE CASCADE,
FOREIGN KEY(server_id) REFERENCES server_region(server_id),
FOREIGN KEY(environment_id) REFERENCES game_environment(environment_id),
FOREIGN KEY(experience_id) REFERENCES game_experience(experience_id),
FOREIGN KEY(style_id) REFERENCES play_style(style_id) ON DELETE SET NULL,
FOREIGN KEY(legality_id) REFERENCES legality(legality_id),
FOREIGN KEY(group_status_id) REFERENCES group_status(group_status_id),
FOREIGN KEY(category_id) REFERENCES gameplay_category(category_id) ON DELETE SET NULL,
FOREIGN KEY(subcategory_id) REFERENCES gameplay_subcategory(subcategory_id) ON DELETE SET NULL,
FOREIGN KEY(pvp_status_id) REFERENCES pvp_status(pvp_status_id),
FOREIGN KEY(system_id) REFERENCES sc_fleetfinder.planetary_system(system_id) ON DELETE SET NULL,
FOREIGN KEY(planet_id) REFERENCES sc_fleetfinder.planet_moon_system(planet_id) ON DELETE SET NULL
);

-- Changing autoincrement values for test db to differentiate
ALTER TABLE test_sc_fleetfinder.game_environment AUTO_INCREMENT = 100;
ALTER TABLE test_sc_fleetfinder.game_experience AUTO_INCREMENT = 100;
ALTER TABLE test_sc_fleetfinder.gameplay_category AUTO_INCREMENT = 100;
ALTER TABLE test_sc_fleetfinder.gameplay_subcategory AUTO_INCREMENT = 100;
ALTER TABLE test_sc_fleetfinder.group_listing AUTO_INCREMENT = 100;
ALTER TABLE test_sc_fleetfinder.planet_moon_system AUTO_INCREMENT = 100;
ALTER TABLE test_sc_fleetfinder.planetary_system AUTO_INCREMENT = 100;
ALTER TABLE test_sc_fleetfinder.play_style AUTO_INCREMENT = 100;
ALTER TABLE test_sc_fleetfinder.server_region AUTO_INCREMENT = 100;
ALTER TABLE test_sc_fleetfinder.`user` AUTO_INCREMENT = 100;

-- gameplay_category insert
INSERT INTO test_sc_fleetfinder.gameplay_category (category_name)
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
    ('Event');

-- retrieving category id's
/*
SELECT category_id, category_name
FROM gameplay_category
WHERE category_name IN ('Ship Combat', 'FPS Combat', 'Piracy', 'Cargo/Trade', 'Mining', 'Salvage', 
		'Mission', 'Exploration', 'Medical', 'Racing', 'Event'); 
*/

-- gameplay_sub_category insert
INSERT INTO test_sc_fleetfinder.gameplay_subcategory (subcategory_name, category_id)
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
    ('Cargo Hauling - LAWFUL', (SELECT category_id FROM gameplay_category WHERE category_name = 'Cargo/Trade')),
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
INSERT INTO test_sc_fleetfinder.planetary_system (system_name)
VALUES
	('Stanton'),
    ('Pyro');


-- Retrieving planetary_system id's    
/*
SELECT system_id, system_name
FROM planetary_system
WHERE system_name IN ('Stanton', 'Pyro');
*/

-- Inserting planet/moon systems
INSERT INTO test_sc_fleetfinder.planet_moon_system (planet_name, system_id)
VALUES
	('Hurston: Stanton I', (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton')),
    ('Crusader: Stanton II', (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton')),
    ('ArcCorp: Stanton III', (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton')),
    ('microTech: Stanton IV', (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton')),
    ('Other - Stanton', (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton')),
    ('Pyro I', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro')),
    ('Monox: Pyro II', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro')),
    ('Bloom: Pyro III', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro')),
    ('Pyro IV', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro')),
    ('Pyro V', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro')),
    ('Terminus: Pyro VI', (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro'));
    
-- Inserting available server regions
INSERT INTO test_sc_fleetfinder.server_region (server_name)
VALUES
	('USA'),
    ('EU'),
    ('AUS'),
    ('Asia'),
    ('Any');

-- Inserting available game environments
INSERT INTO test_sc_fleetfinder.game_environment (environment_type)
VALUES
	('LIVE'),
    ('PTU'),
    ('Tech Preview'),
    ('Any');
    
-- Inserting available game experiences
INSERT INTO test_sc_fleetfinder.game_experience (experience_type)
VALUES
	('Persistent Universe'),
    ('Arena Commander');
    
-- Inserting suggested play style options
INSERT INTO test_sc_fleetfinder.play_style (play_style)
VALUES
	('Casual'),
    ('Competitive'),
    ('Learning'),
    ('Social'),
    ('For Hire');