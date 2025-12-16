package com.sc_fleetfinder.fleets.DAO.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReport;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;

@RepositoryRestResource(exported = false)
public interface ListingReportRepository extends JpaRepository<ListingReport, Long> {

    @Query("select lr.listingRef.groupId from ListingReport lr where lr.reportingUserRef = :user")
    Set<Long> findByReportingUserRef(@Param("user") Users user);

    Set<ListingReport> findByModIssueRef(ModerationIssue issue);
}
