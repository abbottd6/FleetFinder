package com.sc_fleetfinder.fleets;

import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class PlanetarySystemTest {

    @Autowired
    private PlanetarySystemRepository planetarySystemRepository;

    @Test
    public void testPlanetarySystemsPopulating() {
        int systemId = 1;
        PlanetarySystem fetchedSystem = planetarySystemRepository.findById(systemId).orElse(null);

        assertNotNull(fetchedSystem, "System should not be null");
        assertNotNull(fetchedSystem.getPlanetMoonSystems(), "System planet/moons set should not be null");
        assertFalse(fetchedSystem.getPlanetMoonSystems().isEmpty(), "Planetary subsystems should not be empty");

        fetchedSystem.getPlanetMoonSystems().forEach(sub -> {
            assertNotNull(sub.getPlanetName(), "Planet name should not be null");
            assertEquals(systemId, sub.getPlanetarySystem().getSystemId(), "Planetary system id should be equal to " + systemId);
        });
    }
}
