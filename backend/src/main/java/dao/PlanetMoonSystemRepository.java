package dao;

import entities.PlanetMoonSystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanetMoonSystemRepository extends JpaRepository<PlanetMoonSystem, Integer> {
}
