-- TEST Listing
-- SELECT * FROM `user`;

INSERT INTO sc_fleetfinder.group_listing (id_user, server_region, game_environment, game_experience, listing_title, listing_user, 
	engagement_type, legality, group_status, event_scheduled, category_id, subcategory_id, 
    pvp_status, system_id, planet_id, activity_description, desired_party_size, available_roles, comms_options,
    comms_service)
VALUES 
	((SELECT id_user FROM `user` WHERE id_user = (SELECT MIN(id_user) FROM `user`)), 'USA', 'LIVE', 'Persistent Universe', 'Mining with a cutty black and ROC on Aberdeen', 
    (SELECT user_name FROM `user` WHERE id_user = (SELECT MIN(id_user) FROM `user`)), 'Casual', 'LAWFUL', 'FUTURE', '2024-12-24 12:30:00', 
    (SELECT category_id FROM gameplay_category AS gc WHERE gc.category_name = 'Mining'), 
    (SELECT subcategory_id FROM gameplay_subcategory AS gsc WHERE gsc.subcategory_name = 'Ground Vehicle Mining'), 'PVP', (SELECT system_id FROM planetary_system WHERE system_name = 'Stanton'), 
    (SELECT planet_id FROM planet_moon_system WHERE planet_name = 'Hurston: Stanton I'), 'Looking for someone to scout/guard while I mine in my ROC. Profit split 50/50.', '2', 'Pilot', 'REQUIRED', 'Discord');
    
-- SELECT * FROM `user`;
-- SELECT * FROM gameplay_category;
-- SELECT * FROM gameplay_subcategory WHERE category_id = '3';
-- SELECT * FROM planetary_system;
-- SELECT * FROM planet_moon_system;
 SELECT * FROM group_listing;