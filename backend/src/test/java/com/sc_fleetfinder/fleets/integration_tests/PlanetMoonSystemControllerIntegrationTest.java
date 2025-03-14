package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = TestEnvironmentLoader.class)
public class PlanetMoonSystemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    //testing that correct number of planets exist
    @Test
    void testGetAllPlanetMoons_Success_200() throws Exception {
        mockMvc.perform(get("/api/planetMoonSystems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(12))
                .andExpect(jsonPath("$[0].planetId").exists())
                .andExpect(jsonPath("$[0].planetName").exists())
                .andExpect(jsonPath("$[0].systemName").exists());
    }

    //testing planet attributes
    @Test
    void testGetPlanetByIdSuccess_200() throws Exception {
        mockMvc.perform(get("/api/planetMoonSystems/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planetId").value(1))
                .andExpect(jsonPath("$.planetName").value("Hurston: Stanton I"))
                .andExpect(jsonPath("$.systemName").value("Stanton"));
    }

    @Test
    void testGetPlanetByIdFailure_404() throws Exception {
        mockMvc.perform(get("/api/planetMoonSystems/500"))
                .andExpect(status().isNotFound());
    }
}
