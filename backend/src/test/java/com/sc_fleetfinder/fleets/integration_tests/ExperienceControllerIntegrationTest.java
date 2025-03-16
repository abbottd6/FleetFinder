package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = TestEnvironmentLoader.class)
public class ExperienceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    //testing that correct number of game experiences exist
    @Test
    void testGetAllGameExperiences_Success_200() throws Exception {
        mockMvc.perform(get("/api/lookup/game-experiences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].experienceId").exists())
                .andExpect(jsonPath("$[0].experienceType").exists());
    }

    //testing responseDto values
    @Test
    void testGetExperienceById_Success_200() throws Exception {
        mockMvc.perform(get("/api/lookup/game-experiences/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.experienceId").value(1))
                .andExpect(jsonPath("$.experienceType").value("Persistent Universe"));
    }

    @Test
    void testGetExperienceById_Failure_404() throws Exception {
        mockMvc.perform(get("/api/lookup/game-experiences/500"))
                .andExpect(status().isNotFound());
    }
}
