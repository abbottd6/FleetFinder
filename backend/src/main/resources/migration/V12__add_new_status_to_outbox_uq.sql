/* TODO move into the create table and set entity_new_status to not null */

ALTER TABLE notification_outbox
    DROP INDEX uq_note_outbox_event,
    ADD UNIQUE KEY uq_note_outbox_event (
        event_type,
        entity_type,
        entity_id,
        entity_new_status
    )