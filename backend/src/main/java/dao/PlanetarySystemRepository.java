package dao;

import entities.PlanetarySystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanetarySystemRepository extends JpaRepository<PlanetarySystem, Integer> {
}
