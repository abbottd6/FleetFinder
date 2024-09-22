package com.sc_fleetfinder.fleets;

import com.sc_fleetfinder.fleets.dao.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
class FleetsApplicationTests {

	@Test
	void contextLoads() {
	}

}
