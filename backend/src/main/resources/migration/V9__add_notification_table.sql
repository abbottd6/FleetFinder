CREATE TABLE IF NOT EXISTS notification
(
    id_notification BIGINT PRIMARY KEY                     NOT NULL AUTO_INCREMENT,
    id_user         BIGINT                                 NOT NULL,
    type            VARCHAR(25)                            NOT NULL,
    title           VARCHAR(80)                            NULL,
    message         TEXT(255)                              NOT NULL,
    id_action       BIGINT                                 NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP    NOT NULL,
    read_at         DATETIME                               NULL,

    CONSTRAINT note_fk_ref_user
        FOREIGN KEY (id_user) REFERENCES users (id_user)
            ON DELETE CASCADE,

    CONSTRAINT note_fk_ref_mod_action
        FOREIGN KEY (id_action) REFERENCES mod_listing_action (id_action)
            ON DELETE SET NULL
)