package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.GameExperience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<GameExperience, Integer> {
}
