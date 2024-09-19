package dao;

import entities.ServerRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRegionRepository extends JpaRepository<ServerRegion, Integer> {
}
