-- TEST Users
INSERT INTO test_sc_fleetfinder.`user` (user_name, user_password, email, server_id, org, about_user)
VALUES 
	('Batman', 'batmanIsCool', 'thebatman@yahoo.com', (SELECT server_id FROM server_region WHERE server_name = 'USA'), 'ICP', 'Love to beat up bad guys in my Vanguard Batmobile'),
    ('Johnny', 'johnnyIsCool', 'johnny@gmail.com', (SELECT server_id FROM server_region WHERE server_name = 'AUS'), 'IAE', 'Career miner, primarily on Aberdeen. Pro scanner.');
    
-- SELECT * FROM `user`;
-- SELECT * FROM gameplay_category;
-- SELECT * FROM gameplay_subcategory WHERE category_id = '3';
-- SELECT * FROM planetary_system;
-- SELECT * FROM planet_moon_system;
-- SELECT * FROM group_listing;
