package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.PvpStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PvpStatusRepository extends JpaRepository<PvpStatus, Integer> {
}
