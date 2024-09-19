package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameplayCategoryRepository extends JpaRepository<GameplayCategory, Integer> {
}
