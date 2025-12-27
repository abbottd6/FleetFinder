package com.sc_fleetfinder.fleets.DAO.ListingReferenceData;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameplayCategoryRepository extends JpaRepository<GameplayCategory, Integer> {

    Optional<GameplayCategory> findByCategoryName(String categoryName);
}
