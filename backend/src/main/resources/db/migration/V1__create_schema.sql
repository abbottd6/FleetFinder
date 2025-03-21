-- flyway configuration

CREATE TABLE IF NOT EXISTS `user` (
id_user BIGINT NOT NULL AUTO_INCREMENT,
user_name VARCHAR(32) NOT NULL,
user_password VARCHAR(32) NOT NULL,
email VARCHAR(45) NOT NULL,
server_id INT,
org VARCHAR(25),
about_user VARCHAR(255),
acct_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (id_user)
);

CREATE TABLE IF NOT EXISTS gameplay_category (
category_id INT NOT NULL AUTO_INCREMENT,
category_name VARCHAR(25) NOT NULL,
PRIMARY KEY(category_id)
);

CREATE TABLE IF NOT EXISTS gameplay_subcategory (
subcategory_id INT NOT NULL AUTO_INCREMENT,
subcategory_name VARCHAR(25) NOT NULL,
category_id INT NOT NULL,
PRIMARY KEY(subcategory_id),
FOREIGN KEY(category_id) REFERENCES gameplay_category(category_id)
);

CREATE TABLE IF NOT EXISTS planetary_system (
system_id INT NOT NULL AUTO_INCREMENT,
system_name VARCHAR(25) NOT NULL,
PRIMARY KEY(system_id)
);

CREATE TABLE IF NOT EXISTS planet_moon_system (
planet_id INT NOT NULL AUTO_INCREMENT,
planet_name VARCHAR(25) NOT NULL,
system_id INT NOT NULL,
PRIMARY KEY(planet_id),
FOREIGN KEY(system_id) REFERENCES planetary_system(system_id)
);

CREATE TABLE IF NOT EXISTS server_region (
server_id INT NOT NULL AUTO_INCREMENT,
server_name VARCHAR(12) NOT NULL,
PRIMARY KEY(server_id)
);

CREATE TABLE IF NOT EXISTS game_environment (
environment_id INT NOT NULL AUTO_INCREMENT,
environment_type VARCHAR(16) NOT NULL,
PRIMARY KEY(environment_id)
);

CREATE TABLE IF NOT EXISTS game_experience (
experience_id INT NOT NULL AUTO_INCREMENT,
experience_type VARCHAR(20) NOT NULL,
PRIMARY KEY(experience_id)
);

CREATE TABLE IF NOT EXISTS play_style (
style_id INT NOT NULL AUTO_INCREMENT,
play_style VARCHAR(17),
PRIMARY KEY(style_id)
);

CREATE TABLE IF NOT EXISTS group_status (
group_status_id INT NOT NULL AUTO_INCREMENT,
group_status VARCHAR(16),
PRIMARY KEY(group_status_id)
);

CREATE TABLE IF NOT EXISTS legality (
legality_id INT NOT NULL AUTO_INCREMENT,
legality VARCHAR(9) NOT NULL,
PRIMARY KEY(legality_id)
);

CREATE TABLE IF NOT EXISTS pvp_status (
pvp_status_id INT NOT NULL AUTO_INCREMENT,
pvp_status VARCHAR(3) NOT NULL,
PRIMARY KEY(pvp_status_id)
);

CREATE TABLE IF NOT EXISTS group_listing (
id_group BIGINT NOT NULL AUTO_INCREMENT,
id_user BIGINT NOT NULL,
server_id INT NOT NULL,
environment_id INT NOT NULL,
experience_id INT NOT NULL,
listing_title VARCHAR(65) NOT NULL,
style_id INT,
legality_id INT NOT NULL,
group_status_id INT NOT NULL,
event_schedule TIMESTAMP,
category_id INT NOT NULL,
subcategory_id INT,
pvp_status_id INT NOT NULL,
system_id INT NOT NULL,
planet_id INT,
listing_description VARCHAR(500) NOT NULL,
desired_party_size TINYINT NOT NULL,
current_party_size TINYINT NOT NULL,
available_roles VARCHAR(255),
comms_options ENUM('Required', 'Optional', 'No Comms') NOT NULL,
comms_service VARCHAR(50),
last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
creation_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
PRIMARY KEY(id_group),
FOREIGN KEY(id_user) REFERENCES `user`(id_user) ON DELETE CASCADE,
FOREIGN KEY(server_id) REFERENCES server_region(server_id),
FOREIGN KEY(environment_id) REFERENCES game_environment(environment_id),
FOREIGN KEY(experience_id) REFERENCES game_experience(experience_id),
FOREIGN KEY(style_id) REFERENCES play_style(style_id),
FOREIGN KEY(legality_id) REFERENCES legality(legality_id),
FOREIGN KEY(group_status_id) REFERENCES group_status(group_status_id),
FOREIGN KEY(category_id) REFERENCES gameplay_category(category_id),
FOREIGN KEY(subcategory_id) REFERENCES gameplay_subcategory(subcategory_id),
FOREIGN KEY(pvp_status_id) REFERENCES pvp_status(pvp_status_id),
FOREIGN KEY(system_id) REFERENCES planetary_system(system_id),
FOREIGN KEY(planet_id) REFERENCES planet_moon_system(planet_id)
);