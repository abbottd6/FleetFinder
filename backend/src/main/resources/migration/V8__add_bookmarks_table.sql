CREATE TABLE listing_bookmark (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_user BIGINT NOT NULL,
    id_group BIGINT NOT NULL,
    creation_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT unique_user_listing UNIQUE (id_user, id_group),
    CONSTRAINT bookmark_fk_user FOREIGN KEY (id_user) REFERENCES users(id_user),
    CONSTRAINT bookmark_fk_listing FOREIGN KEY (id_group) REFERENCES group_listing(id_group)
)