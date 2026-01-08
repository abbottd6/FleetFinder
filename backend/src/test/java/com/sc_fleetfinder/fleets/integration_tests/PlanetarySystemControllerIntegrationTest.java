package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.testConfig.SimpMessageTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = TestEnvironmentLoader.class, classes = SimpMessageTestConfig.class)
public class PlanetarySystemControllerIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private MockMvc mockMvc;

    //testing correct number of planetary systems exist
    @Test
    void testGetAllPlanetarySystems_Success_200() throws Exception {
        mockMvc.perform(get("/api/lookup/planetary-systems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].systemId").exists())
                .andExpect(jsonPath("$[0].systemName").exists());
    }

    //testing response dto values
    @Test
    void testGetPlanetarySystemByIdSuccess_200() throws Exception {
        mockMvc.perform(get("/api/lookup/planetary-systems/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.systemId").value(1))
                .andExpect(jsonPath("$.systemName").value("Stanton"));
    }

    @Test
    void testGetPlanetarySystemByIdFailure_404() throws Exception {
        mockMvc.perform(get("/api/lookup/planetary-systems/500"))
                .andExpect(status().isNotFound());
    }
}
