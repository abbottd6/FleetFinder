CREATE TABLE listing_report_basis
(
    id_basis    INT PRIMARY KEY AUTO_INCREMENT,
    basis_label VARCHAR(25) NOT NULL
);

INSERT INTO listing_report_basis (basis_label)
VALUE
    ('Spam'),
    ('Hate Speech'),
    ('NSFW'),
    ('Scam/Fraud'),
    ('Off Topic'),
    ('Low Quality/Troll'),
    ('Doxxing/Personal Info'),
    ('Cheating/RMT'),
    ('Other');

CREATE TABLE moderation_issue
(
    id_issue           BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_group           BIGINT                                             NOT NULL,
    id_user            BIGINT                                             NOT NULL,
    report_total_count INT                                                NOT NULL DEFAULT 0,
    spam_count         INT                                                NOT NULL DEFAULT 0,
    hate_speech_count  INT                                                NOT NULL DEFAULT 0,
    nsfw_count         INT                                                NOT NULL DEFAULT 0,
    scam_count         INT                                                NOT NULL DEFAULT 0,
    off_topic_count    INT                                                NOT NULL DEFAULT 0,
    troll_count        INT                                                NOT NULL DEFAULT 0,
    doxx_count         INT                                                NOT NULL DEFAULT 0,
    cheat_count        INT                                                NOT NULL DEFAULT 0,
    other_count        INT                                                NOT NULL DEFAULT 0,
    first_report_ts    TIMESTAMP,
    last_report_ts     TIMESTAMP,
    status             ENUM ('TBD', 'Clear', 'Actioned') NOT NULL DEFAULT 'TBD',

    CONSTRAINT mod_issue_fk__ref_group
        FOREIGN KEY (id_group)
            REFERENCES group_listing (id_group)
            ON DELETE CASCADE,
    CONSTRAINT mod_issue_fk_ref_user
        FOREIGN KEY (id_user)
            REFERENCES users (id_user)
            ON DELETE CASCADE
);

CREATE TABLE listing_report
(
    id_report   BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_issue    BIGINT                                    NOT NULL,
    id_group    BIGINT                                    NOT NULL,
    id_reporter BIGINT                                    NOT NULL,
    id_basis    INT                                       NOT NULL,
    # Add JPA table join with listing_report_basis for basis label
    created_at  TIMESTAMP                                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status      ENUM ('Pending', 'Actioned', 'Dismissed') NOT NULL DEFAULT 'Pending',

    CONSTRAINT listing_report_fk_ref_group
        FOREIGN KEY (id_group)
            REFERENCES group_listing (id_group)
            ON DELETE CASCADE,
    CONSTRAINT listing_report_fk_ref_report_basis
        FOREIGN KEY (id_basis)
            REFERENCES listing_report_basis (id_basis),
    CONSTRAINT listing_report_fk_ref_issue
        FOREIGN KEY (id_issue)
            REFERENCES moderation_issue (id_issue)
            ON DELETE CASCADE,
    CONSTRAINT uq_reporter_to_listing
        UNIQUE (id_group, id_reporter)
);

CREATE TABLE user_moderation_record
(
    id_user BIGINT PRIMARY KEY NOT NULL,
    # add JPA table join with users for user details
    mod_action_count INT NOT NULL DEFAULT 0,
    # add JPA table join on moderator actions for overview of mod actions
    is_banned TINYINT(1) NOT NULL DEFAULT 0,
    last_mod_action TIMESTAMP,

    CONSTRAINT mod_record_fk_ref_user
        FOREIGN KEY (id_user)
            REFERENCES users (id_user)
            ON DELETE CASCADE
);

CREATE TABLE mod_listing_action
(
    id_action   BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_archive  BIGINT                  NOT NULL,
    id_user     BIGINT                  NOT NULL,
    username    VARCHAR(32)             NOT NULL,
    id_mod      BIGINT,
    mod_name    VARCHAR(32),
    action_type ENUM ('AutoMod', 'Manual') NOT NULL DEFAULT 'Manual',
    action_note VARCHAR(255),
    action_ts   TIMESTAMP               NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT mod_action_fk_ref_listing_archive
        FOREIGN KEY (id_archive)
            REFERENCES listing_archive (id_archive)
);