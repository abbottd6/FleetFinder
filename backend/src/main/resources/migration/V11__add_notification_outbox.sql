CREATE TABLE IF NOT EXISTS notification_outbox
(
    outbox_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type        VARCHAR(64)   NOT NULL,
    entity_type       VARCHAR(64)   NOT NULL,
    entity_id         BIGINT        NOT NULL,
    entity_owner_id   BIGINT        NOT NULL,
    entity_new_status VARCHAR(12)   NULL,
    payload_json      JSON          NULL,
    status            VARCHAR(16)   NOT NULL DEFAULT 'PENDING',
    attempt_count     INT           NOT NULL DEFAULT 0,
    last_error        VARCHAR(1000) NULL,
    created_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    locked_at         TIMESTAMP     NULL,
    sent_at           TIMESTAMP     NULL,
    FOREIGN KEY (entity_owner_id) REFERENCES `users` (id_user) ON DELETE CASCADE,
    UNIQUE KEY uq_note_outbox_event (event_type, entity_type, entity_id, entity_new_status),
    KEY idx_on_outbox_status_and_created (status, created_at)
);