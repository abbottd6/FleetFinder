package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.PvpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@RepositoryRestResource(exported = false)
public interface PvpStatusRepository extends JpaRepository<PvpStatus, Integer> {
}
