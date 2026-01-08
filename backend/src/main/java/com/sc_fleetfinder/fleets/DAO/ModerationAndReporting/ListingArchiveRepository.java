package com.sc_fleetfinder.fleets.DAO.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ListingArchiveRepository extends JpaRepository<ListingArchive, Long> {

    Optional<ListingArchive> findByGroupId(Long groupId);

    @Modifying
    @Query(value = """
            INSERT INTO listing_archive (
            id_group, id_user, username, listing_title,
            listing_description, listing_roles, comms_service,
            server_id, environment_id, experience_id, style_id,
            legality_id, group_status_id, event_schedule, category_id,
            subcategory_id, pvp_status_id, system_id, planet_id, current_party_size,
            desired_party_size, comms_options, listing_creation_ts, listing_last_updated,

            report_total_count, spam_count, hate_speech_count, nsfw_count, scam_count,
            off_topic_count, troll_count, doxx_count, cheat_count, other_count, status,

            id_mod, modname, action_type, action_note, archive_ts
            )
            SELECT
                gl.id_group                     AS id_group,
                gl.id_user                      AS id_user,
                u.user_name                     AS username,

                gl.listing_title                AS listing_title,
                gl.listing_description          AS listing_description,
                gl.available_roles              AS listing_roles,

                gl.comms_service                AS comms_service,
                gl.server_id                    AS server_id,
                gl.environment_id               AS environment_id,
                gl.experience_id                AS experience_id,
                gl.style_id                     AS style_id,

                gl.legality_id                  AS legality_id,
                gl.group_status_id              AS group_status_id,
                gl.event_schedule               AS event_schedule,

                gl.category_id                  AS category_id,
                gl.subcategory_id               AS subcategory_id,
                gl.pvp_status_id                AS pvp_status_id,
                gl.system_id                    AS system_id,
                gl.planet_id                    AS planet_id,

                gl.current_party_size           AS current_party_size,
                gl.desired_party_size           AS desired_party_size,
                gl.comms_options                AS comms_options,

                gl.creation_timestamp           AS listing_creation_ts,
                gl.last_updated                 AS listing_last_updated,

            COALESCE(mi.report_total_count, 0)  AS report_total_count,
            COALESCE(mi.spam_count, 0)          AS spam_count,
            COALESCE(mi.hate_speech_count, 0)   AS hate_speech_count,
            COALESCE(mi.nsfw_count, 0)          AS nsfw_count,
            COALESCE(mi.scam_count, 0)          AS scam_count,
            COALESCE(mi.off_topic_count, 0)     AS off_topic_count,
            COALESCE(mi.troll_count, 0)         AS troll_count,
            COALESCE(mi.doxx_count, 0)          AS doxx_count,
            COALESCE(mi.cheat_count, 0)         AS cheat_count,
            COALESCE(mi.other_count, 0)         AS other_count,
            COALESCE(mi.status, 'No Reports')   AS status,

            NULL                                AS id_mod,
            NULL                                AS modname,
            'None'                              AS action_type,
            'Scheduled archive'                 AS action_note,

            NOW()                               AS archive_ts

            FROM group_listing gl
            JOIN users u
                ON u.id_user = gl.id_user
            LEFT JOIN moderation_issue mi
                ON mi.id_group = gl.id_group
            WHERE gl.vis_status = 'ARCHIVED'
                AND gl.last_updated < (NOW() - INTERVAL 14 DAY)
                AND (gl.event_schedule IS NULL OR gl.event_schedule < (NOW() - INTERVAL 14 DAY))
            ON DUPLICATE KEY UPDATE id_archive = id_archive
            """, nativeQuery = true)
    int generateArchivesForScheduledRemoval();
}
