ALTER TABLE notification_outbox
    ADD COLUMN delivery_channel VARCHAR(16) NOT NULL DEFAULT 'IN_APP',
    DROP INDEX uq_note_outbox_event,
    ADD UNIQUE KEY uq_note_outbox_event (event_type, entity_type, entity_id,
                                         entity_owner_id, entity_new_status, delivery_channel);

ALTER TABLE new_listing_notify_queue
    ADD KEY index_on_new_listing_queue_status_and_queued_at (status, queued_at);
