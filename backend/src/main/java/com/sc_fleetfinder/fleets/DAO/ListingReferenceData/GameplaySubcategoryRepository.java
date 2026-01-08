package com.sc_fleetfinder.fleets.DAO.ListingReferenceData;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplaySubcategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameplaySubcategoryRepository extends JpaRepository<GameplaySubcategory, Integer> {
}
