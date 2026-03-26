package com.sc_fleetfinder.fleets.DAO.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ModerationIssueRepository extends JpaRepository<ModerationIssue, Long> {

    Optional<ModerationIssue> findByGroupRef(GroupListing groupListing);

    @Query(value = """
            SELECT basis.id_basis
            FROM listing_report lr
            JOIN listing_report_basis basis ON lr.id_basis = basis.id_basis
            WHERE lr.id_issue = :issueId
            GROUP BY lr.id_basis
            ORDER BY COUNT(*) DESC
            LIMIT 1
            """, nativeQuery = true)
    Integer findAutoModActionBasis_MostCommonReportBasis(@Param("issueId") Long issueId);
}
