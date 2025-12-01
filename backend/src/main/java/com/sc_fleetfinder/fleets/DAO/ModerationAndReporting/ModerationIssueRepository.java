package com.sc_fleetfinder.fleets.DAO.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ModerationIssueRepository extends JpaRepository<ModerationIssue, Long> {
}
