package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.Legality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegalityRepository extends JpaRepository<Legality, Integer> {
}
