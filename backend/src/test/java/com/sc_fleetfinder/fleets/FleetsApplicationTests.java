package com.sc_fleetfinder.fleets;

import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.integration_tests.AbstractIntegrationTestDB;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ContextConfiguration(initializers = TestEnvironmentLoader.class)
@ActiveProfiles("test")
class FleetsApplicationTests extends AbstractIntegrationTestDB {

	@Test
	void contextLoads() {
	}

}
