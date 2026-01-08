CREATE TABLE listing_archive
(
    id_archive           BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_group             BIGINT                                    NOT NULL,
    id_user              BIGINT                                    NOT NULL,
    username             VARCHAR(32)                               NOT NULL,
    listing_title        VARCHAR(65)                               NOT NULL,
    listing_description  VARCHAR(500)                              NOT NULL,
    listing_roles        VARCHAR(255),
    comms_service        VARCHAR(50),
    server_id            INT                                       NOT NULL,
    environment_id       INT                                       NOT NULL,
    experience_id        INT                                       NOT NULL,
    style_id             INT,
    legality_id          INT                                       NOT NULL,
    group_status_id      INT                                       NOT NULL,
    event_schedule       TIMESTAMP,
    category_id          INT                                       NOT NULL,
    subcategory_id       INT,
    pvp_status_id        INT                                       NOT NULL,
    system_id            INT                                       NOT NULL,
    planet_id            INT,
    current_party_size   TINYINT                                   NOT NULL,
    desired_party_size   TINYINT                                   NOT NULL,
    comms_options        ENUM ('Required', 'Optional', 'No Comms') NOT NULL,
    listing_creation_ts  TIMESTAMP                                 NOT NULL,
    listing_last_updated TIMESTAMP                                 NOT NULL,
    report_total_count   INT                                       NOT NULL DEFAULT 0,
    spam_count           INT                                       NOT NULL DEFAULT 0,
    hate_speech_count    INT                                       NOT NULL DEFAULT 0,
    nsfw_count           INT                                       NOT NULL DEFAULT 0,
    scam_count           INT                                       NOT NULL DEFAULT 0,
    off_topic_count      INT                                       NOT NULL DEFAULT 0,
    troll_count          INT                                       NOT NULL DEFAULT 0,
    doxx_count           INT                                       NOT NULL DEFAULT 0,
    cheat_count          INT                                       NOT NULL DEFAULT 0,
    other_count          INT                                       NOT NULL DEFAULT 0,
    status               ENUM ('Pending', 'No Reports', 'Cleared', 'Actioned'),
    id_mod               BIGINT,
    modname              VARCHAR(32),
    action_type          ENUM ('None', 'AutoMod', 'Manual')        NOT NULL DEFAULT 'None',
    action_note          VARCHAR(255),
    archive_ts           TIMESTAMP                                 NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT archive_fk_ref_server
        FOREIGN KEY (server_id)
            REFERENCES server_region(server_id),
    CONSTRAINT archive_fk_ref_environment
        FOREIGN KEY (environment_id)
            REFERENCES game_environment(environment_id),
    CONSTRAINT archive_fk_ref_experience
        FOREIGN KEY (experience_id)
            REFERENCES game_experience(experience_id),
    CONSTRAINT archive_fk_ref_play_style
        FOREIGN KEY (style_id)
            REFERENCES play_style(style_id),
    CONSTRAINT archive_fk_ref_legality
        FOREIGN KEY (legality_id)
            REFERENCES legality(legality_id),
    CONSTRAINT archive_fk_ref_group_status
        FOREIGN KEY (group_status_id)
            REFERENCES group_status(group_status_id),
    CONSTRAINT archive_fk_ref_gameplay_category
        FOREIGN KEY (category_id)
            REFERENCES gameplay_category(category_id),
    CONSTRAINT archive_fk_ref_gameplay_subcategory
        FOREIGN KEY (subcategory_id)
            REFERENCES gameplay_subcategory(subcategory_id),
    CONSTRAINT archive_fk_ref_pvp_status
        FOREIGN KEY (pvp_status_id)
            REFERENCES pvp_status(pvp_status_id),
    CONSTRAINT archive_fk_ref_planetary_system
        FOREIGN KEY (system_id)
            REFERENCES planetary_system(system_id),
    CONSTRAINT archive_fk_ref_planet_moon_system
        FOREIGN KEY (planet_id)
            REFERENCES planet_moon_system(planet_id),
    CONSTRAINT uq_archive_per_listing
        UNIQUE KEY (id_group)
);