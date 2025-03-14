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
public class PlayStyleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    //testing correct number of play styles
    @Test
    void testGetAllPlayStyles_Success_200() throws Exception {
        mockMvc.perform(get("/api/playStyles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(11))
                .andExpect(jsonPath("$[0].styleId").exists())
                .andExpect(jsonPath("$[0].playStyle").exists());
    }

    //testing play style attribute values
    @Test
    void testGetPlayStyleById_Success_200() throws Exception {
        mockMvc.perform(get("/api/playStyles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.styleId").value(1))
                .andExpect(jsonPath("$.playStyle").value("Casual"));
    }

    @Test
    void testGetPlayStyleById_Failure_404() throws Exception {
        mockMvc.perform(get("/api/playStyles/500"))
                .andExpect(status().isNotFound());
    }
}
