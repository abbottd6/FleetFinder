package com.sc_fleetfinder.fleets.DAO.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ListingReportRepository extends JpaRepository<ListingReport, Long> {
}
