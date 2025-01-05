-- TEST Listing
-- SELECT * FROM `user`;

INSERT INTO sc_fleetfinder.group_listing (id_user, server_id, environment_id, experience_id, listing_title, 
	style_id, legality_id, group_status_id, event_schedule, category_id, subcategory_id, 
    pvp_status_id, system_id, planet_id, listing_description, desired_party_size, current_party_size, available_roles, comms_options,
    comms_service)
VALUES 
	((SELECT id_user FROM `user` WHERE id_user = (SELECT MIN(id_user) FROM `user`)), (SELECT server_id FROM server_region WHERE server_name = 'USA') , (SELECT environment_id FROM game_environment WHERE environment_type = 'LIVE'),
    (SELECT experience_id FROM game_experience WHERE experience_type = 'Persistent Universe'), 'Mining with a cutty black and ROC on Aberdeen', 
	(SELECT style_id FROM play_style WHERE play_style = 'Casual'), (SELECT legality_id FROM legality WHERE legality = 'Undefined'), (SELECT group_status_id FROM group_status WHERE group_status = 'Active'),
    '2024-12-24 12:30:00', (SELECT category_id FROM gameplay_category AS gc WHERE gc.category_name = 'Mining'), 
    (SELECT subcategory_id FROM gameplay_subcategory AS gsc WHERE gsc.subcategory_name = 'Ground Vehicle Mining'), (SELECT pvp_status_id FROM pvp_status WHERE pvp_status = 'PvX'), (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton'), 
    (SELECT planet_id FROM planet_moon_system WHERE planet_name = 'Hurston: Stanton I'), 'Looking for someone to scout/guard while I mine in my ROC. Profit split 50/50.', '2', '1', 'Pilot', 'REQUIRED', 'Discord');
    
-- SELECT * FROM `user`;
-- SELECT * FROM gameplay_category;
-- SELECT * FROM gameplay_subcategory;
-- SELECT * FROM planetary_system;
-- SELECT * FROM planet_moon_system;
 -- SELECT * FROM group_listing;
 
 INSERT INTO sc_fleetfinder.group_listing (id_user, server_id, environment_id, experience_id, listing_title, 
	style_id, legality_id, group_status_id, event_schedule, category_id, subcategory_id, 
    pvp_status_id, system_id, planet_id, listing_description, desired_party_size, current_party_size, available_roles, comms_options,
    comms_service)
VALUES 
	((SELECT id_user FROM `user` WHERE id_user = (SELECT MIN(id_user) FROM `user`)), (SELECT server_id FROM server_region WHERE server_name = 'USA') , (SELECT environment_id FROM game_environment WHERE environment_type = 'LIVE'),
    (SELECT experience_id FROM game_experience WHERE experience_type = 'Persistent Universe'), 'Bounty Missions', 
	(SELECT style_id FROM play_style WHERE play_style = 'Casual'), (SELECT legality_id FROM legality WHERE legality = 'Lawful'), (SELECT group_status_id FROM group_status WHERE group_status = 'Active'),
    '2024-12-24 12:30:00', (SELECT category_id FROM gameplay_category AS gc WHERE gc.category_name = 'Ship Combat'), 
    (SELECT subcategory_id FROM gameplay_subcategory AS gsc WHERE gsc.subcategory_name = 'Bounty Hunting PVP'), (SELECT pvp_status_id FROM pvp_status WHERE pvp_status = 'PvP'), (SELECT system_id FROM planetary_system WHERE system_name = 'Pyro'), 
    (SELECT planet_id FROM planet_moon_system WHERE planet_name = 'Bloom: Pyro III'), 'Need a turret gunner for my Vanguard Sentinel', '4', '1', 'Gunner', 'REQUIRED', 'Discord');
    
