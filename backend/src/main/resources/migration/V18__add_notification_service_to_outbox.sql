ALTER TABLE notification_outbox
    ADD COLUMN delivery_channel VARCHAR(16) NOT NULL DEFAULT 'IN_APP',
    DROP INDEX uq_note_outbox_event,
    ADD UNIQUE KEY uq_note_outbox_event (event_type, entity_type, entity_id,
                                         entity_new_status, delivery_channel);
