package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.PvpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@RepositoryRestResource
@CrossOrigin
public interface PvpStatusRepository extends JpaRepository<PvpStatus, Integer> {
}
