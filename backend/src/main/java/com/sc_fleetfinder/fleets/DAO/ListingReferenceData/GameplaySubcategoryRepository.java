package com.sc_fleetfinder.fleets.DAO.ListingReferenceData;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplaySubcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameplaySubcategoryRepository extends JpaRepository<GameplaySubcategory, Integer> {

    @Query(value = """
        SELECT * FROM gameplay_subcategory
        ORDER BY CASE WHEN subcategory_name LIKE '%Other%' THEN 1 ELSE 0 END ASC,
                 subcategory_name ASC;
        """, nativeQuery = true)
    List<GameplaySubcategory> findAll();
}
