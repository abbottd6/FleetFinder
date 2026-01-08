package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, Long> {

    @Modifying
    @Query(value = """
            UPDATE notification_outbox
            SET status = 'PROCESSING',
                locked_at = NOW(),
                attempt_count = attempt_count + 1
            WHERE status = 'PENDING'
                AND (locked_at IS NULL OR locked_at < (NOW() - INTERVAL 10 MINUTE))
            ORDER BY created_at
            LIMIT :limit
            """, nativeQuery = true)
    int claimStatus_Pending(@Param("limit") int limit);

    @Query(value = """
            SELECT * FROM notification_outbox
            WHERE status = 'PROCESSING' AND locked_at < (NOW() - INTERVAL 10 MINUTE)
            ORDER BY created_at
            LIMIT :limit
            """, nativeQuery = true)
    List<NotificationOutbox> findStatus_Claimed(@Param("limit") int limit);

    @Modifying
    @Query(value = """
            UPDATE notification_outbox
            SET status = 'SENT',
                sent_at = NOW()
            WHERE outbox_id = :outboxId
            """, nativeQuery = true)
    int markSent(@Param("outboxId") long outboxId);

    @Modifying
    @Query(value = """
            UPDATE notification_outbox
            SET status = 'FAILED',
                last_error = :error
            WHERE outbox_id = :outboxId
            """, nativeQuery = true)
    int markFailed(@Param("outboxId") long outboxId, @Param("error") String error);
}
