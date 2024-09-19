package dao;

import entities.GameplaySubcategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameplaySubcategoryRepository extends JpaRepository<GameplaySubcategory, Integer> {
}
