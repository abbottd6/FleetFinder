package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.NewListingNotifyQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewListingNotifyQueueRepository extends JpaRepository<NewListingNotifyQueue, Long> {

    @Modifying
    @Query(value = """
            UPDATE new_listing_notify_queue
            SET status = 'PROCESSING', locked_at = NOW()
            WHERE status = 'inQueue'
              AND (locked_at IS NULL OR locked_at < (NOW() - INTERVAL 2 MINUTE))
            ORDER BY queued_at
            LIMIT :limit
            """, nativeQuery = true)
    int claimForProcessing(@Param("limit") int limit);

    @Query(value = """
            SELECT * FROM new_listing_notify_queue
            WHERE status = 'PROCESSING'
              AND locked_at >= (NOW() - INTERVAL 2 MINUTE)
            ORDER BY queued_at
            LIMIT :limit
            """, nativeQuery = true)
    List<NewListingNotifyQueue> findClaimedItems(@Param("limit") int limit);

    @Modifying
    @Query(value = """
            UPDATE new_listing_notify_queue
            SET status = 'PROCESSED', processed_at = NOW()
            WHERE id_group = :groupId
            """, nativeQuery = true)
    int markProcessed(@Param("groupId") long groupId);

    @Modifying
    @Query(value = """
            DELETE FROM new_listing_notify_queue
            WHERE status = 'PROCESSED'
              AND processed_at < (NOW() - INTERVAL 1 DAY)
            """, nativeQuery = true)
    int deleteOldProcessedEntries();
}
