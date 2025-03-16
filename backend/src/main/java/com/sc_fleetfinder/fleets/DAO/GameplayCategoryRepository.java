package com.sc_fleetfinder.fleets.DAO;

import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface GameplayCategoryRepository extends JpaRepository<GameplayCategory, Integer> {

    Optional<GameplayCategory> findByCategoryName(String categoryName);
}
