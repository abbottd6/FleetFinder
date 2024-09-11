-- TEST QUERIES
INSERT INTO `user` (user_name, user_password, email, primary_region, organization, about_user)
VALUES 
	('Batman', 'batmanIsCool', 'thebatman@yahoo.com', 'USA', 'ICP', 'Love to beat up bad guys in my Vanguard Batmobile'),
    ('Johnny', 'johnnyIsCool', 'johnny@gmail.com', 'AUS', 'IAE', 'Career miner, primarily on Aberdeen. Pro scanner.');

SELECT * FROM `user`;
SELECT * FROM gameplay_category;
SELECT * FROM gameplay_subcategory WHERE category_id = '3';
SELECT * FROM planetary_system;
SELECT * FROM planet_moon_system;
SELECT * FROM group_listing;

/*
INSERT INTO group_listing (id_user, server_region, game_environment, game_experience, listing_title, listing_user, 
	engagement_type, legality, group_status, event_scheduled, gameplay_category_id, gameplay_subcategory_id, 
    pvp_status, system_id, planet_id, activity_description, desired_party_size, available_roles, comms_options,
    comms_service)
*/
