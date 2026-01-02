package com.sc_fleetfinder.fleets;

import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.integration_tests.AbstractIntegrationTestDB;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ContextConfiguration(initializers = TestEnvironmentLoader.class)
@ActiveProfiles("test")
class FleetsApplicationTests extends AbstractIntegrationTestDB {

	@TestConfiguration
	static class NoCacheConfig {
		@Bean
		CacheManager cacheManager() {
			return new org.springframework.cache.support.NoOpCacheManager();
		}
	}

}
