package com.sc_fleetfinder.fleets.DAO.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.Instant;

@RepositoryRestResource(exported = false)
public interface ModListingActionRepository extends JpaRepository<ModListingAction, Long> {

    @Query("SELECT act from ModListingAction act WHERE act.actionTs >= :cutoff order by act.actionTs desc")
    Page<ModListingAction> findWeeksActions(@Param("cutoff") Instant cutoff, Pageable pageable);
}
