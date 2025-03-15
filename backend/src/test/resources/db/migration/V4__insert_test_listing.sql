DELETE FROM group_listing WHERE listing_title = 'Integration testing title';

INSERT INTO group_listing (id_user, server_id, environment_id, experience_id, listing_title,
                           style_id, legality_id, group_status_id, event_schedule, category_id, subcategory_id,
                           pvp_status_id, system_id, planet_id, listing_description, desired_party_size,
                           current_party_size, available_roles, comms_options, comms_service)
VALUES
    (1, 1, 1, 1, 'Integration testing title',
     5, 1, 2, '2025-03-15 18:30:00', 7, 27,
     3, 1, 4, 'Preloading test data for integration tests.', 2,
     1, 'Any', 'Optional', 'Discord');
