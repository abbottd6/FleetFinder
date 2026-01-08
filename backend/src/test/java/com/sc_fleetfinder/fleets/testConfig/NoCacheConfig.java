package com.sc_fleetfinder.fleets.testConfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class NoCacheConfig {
    @Bean
    CacheManager cacheManager() {
        return new org.springframework.cache.support.NoOpCacheManager();
    }
}
