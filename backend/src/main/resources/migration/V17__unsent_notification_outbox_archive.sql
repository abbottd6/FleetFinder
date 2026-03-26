/* TODO: MOVE THIS SOMEWHERE MORE APPROPRIATE BEFORE DEPLOY */
ALTER TABLE mod_listing_action
    ADD COLUMN action_basis INT NULL,
    ADD CONSTRAINT fk_mod_action_to_report_basis
        FOREIGN KEY (action_basis) REFERENCES listing_report_basis (id_basis);
/* TODO */

CREATE TABLE IF NOT EXISTS notification_outbox_archive (
    unsent_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    archived_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    outbox_id BIGINT NOT NULL,
    event_type VARCHAR(128) NULL,
    entity_type VARCHAR(128) NULL,
    entity_id BIGINT NULL,
    entity_owner_id BIGINT NULL,
    entity_new_status VARCHAR(32) NULL,
    payload_json JSON NULL,
    status VARCHAR(32) NULL,
    attempt_count TINYINT NULL,
    last_error VARCHAR(2048) NULL,
    created_at TIMESTAMP NULL,
    locked_at TIMESTAMP NULL,
    sent_at TIMESTAMP NULL,
    delivery_channel VARCHAR(32) NULL,
    parent_entity_id BIGINT NULL,
    parent_entity_type VARCHAR(64) NULL,
    sibling_key CHAR(128) NULL,
    error_count TINYINT NULL,
    push_sub_id BIGINT NULL,
    UNIQUE KEY uq_outbox_archive_on_outbox_id (outbox_id)
);

