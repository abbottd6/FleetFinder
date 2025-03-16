package com.sc_fleetfinder.fleets;

import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ContextConfiguration(initializers = TestEnvironmentLoader.class)
class FleetsApplicationTests {

	@Test
	void contextLoads() {
	}

}
