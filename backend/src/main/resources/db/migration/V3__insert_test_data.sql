INSERT INTO `user` (user_name, user_password, email, server_id, org, about_user)
VALUES
    ('Batman', 'batmanIsCool', 'thebatman@yahoo.com', (SELECT server_id FROM server_region WHERE server_name = 'USA'), 'ICP', 'Love to beat up bad guys in my Vanguard Batmobile'),
    ('Johnny', 'johnnyIsCool', 'johnny@gmail.com', (SELECT server_id FROM server_region WHERE server_name = 'AUS'), 'IAE', 'Career miner, primarily on Aberdeen. Pro scanner.'),
    ('TestUser', 'testUserIsTest', 'test@gmail.com', (SELECT server_id FROM server_region WHERE server_name = 'USA'), 'ADP', 'Testing post request.');