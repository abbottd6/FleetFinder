INSERT INTO `user` (user_name, user_password, email, server_id, org, about_user)
VALUES
    ('TestUser', 'testUserIsTest', 'test@gmail.com', (SELECT server_id FROM server_region WHERE server_name = 'USA'), 'ADP', 'Testing post request.');