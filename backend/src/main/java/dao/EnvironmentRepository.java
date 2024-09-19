package dao;

import org.hibernate.cfg.Environment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentRepository extends JpaRepository<Environment, Integer> {
}
