package com.sc_fleetfinder.fleets.DAO.ListingReferenceData;

import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetarySystem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanetarySystemRepository extends JpaRepository<PlanetarySystem, Integer> {
    default List<PlanetarySystem> findAllSorted() {
        return findAll(Sort.by(Sort.Order.asc("sortOrder")));
    }
}
