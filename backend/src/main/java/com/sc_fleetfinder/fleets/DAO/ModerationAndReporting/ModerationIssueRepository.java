package com.sc_fleetfinder.fleets.DAO.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModerationIssueRepository extends JpaRepository<ModerationIssue, Long> {

    Optional<ModerationIssue> findByGroupRef(GroupListing groupListing);
}
