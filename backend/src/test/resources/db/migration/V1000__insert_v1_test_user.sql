DELETE FROM `users` WHERE user_name = 'TestUser';

INSERT INTO `users` (keycloak_id, user_name, email, org, about_user, server_id)
VALUES
    ('someKeycloakId', 'TestUser', 'test@gmail.com', 'ADP', 'Testing post request.',
        (SELECT server_id FROM server_region WHERE server_name = 'USA'));