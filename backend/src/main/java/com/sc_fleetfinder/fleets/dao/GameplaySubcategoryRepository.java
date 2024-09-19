package com.sc_fleetfinder.fleets.dao;

import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameplaySubcategoryRepository extends JpaRepository<GameplaySubcategory, Integer> {
}
