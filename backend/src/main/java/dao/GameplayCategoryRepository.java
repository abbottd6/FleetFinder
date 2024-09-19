package dao;

import entities.GameplayCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameplayCategoryRepository extends JpaRepository<GameplayCategory, Integer> {
}
