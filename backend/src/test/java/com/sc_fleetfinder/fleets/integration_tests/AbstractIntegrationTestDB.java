package com.sc_fleetfinder.fleets.integration_tests;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractIntegrationTestDB {

    private static final MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.2")
                    .withDatabaseName("fleetfinder_integration")
                    .withUsername("testuser")
                    .withPassword("testpassword");

    static {
        mysql.start();
    }

    @DynamicPropertySource
    static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.username", mysql::getUsername);

        registry.add("spring.flyway.enabled", () -> true);

        registry.add("spring.datasource.hikari.maxLifetime", () -> 5_000);      // 5 seconds
        registry.add("spring.datasource.hikari.idleTimeout",  () -> 3_000);      // 3 seconds
        registry.add("spring.datasource.hikari.validationTimeout", () -> 1_000); // 1 second
        // And you can give Hikari a lightweight “ping” query:
        registry.add("spring.datasource.hikari.connectionTestQuery", () -> "SELECT 1");
    }

    @BeforeAll
    static void ensureContainerIsRunning() {
        if (!mysql.isRunning()) {
            mysql.start();
        }
    }
}
