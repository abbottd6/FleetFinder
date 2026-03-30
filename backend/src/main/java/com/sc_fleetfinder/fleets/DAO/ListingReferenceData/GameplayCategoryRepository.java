package com.sc_fleetfinder.fleets.DAO.ListingReferenceData;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameplayCategoryRepository extends JpaRepository<GameplayCategory, Integer> {

    @Query(value = """
        SELECT * FROM gameplay_category
        ORDER BY CASE WHEN category_name = 'Other' THEN 1 ELSE 0 END ASC,
                 category_name ASC;
        """, nativeQuery = true)
    List<GameplayCategory> findAll();

    Optional<GameplayCategory> findByCategoryName(String categoryName);
}
