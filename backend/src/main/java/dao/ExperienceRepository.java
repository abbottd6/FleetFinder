package dao;

import entities.GameExperience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<GameExperience, Integer> {
}
