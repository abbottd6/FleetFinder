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
public class PvpStatusControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    //testing the correct number of pvp statuses exist
    @Test
    void testGetAllPvpStatuses_Success() throws Exception {
        mockMvc.perform(get("/api/pvpStatuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].pvpStatusId").exists())
                .andExpect(jsonPath("$[0].pvpStatus").exists());
    }

    //testing pvp status attribute values
    @Test
    void testGetPvpStatusById_Success() throws Exception {
        mockMvc.perform(get("/api/pvpStatuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pvpStatusId").value(1))
                .andExpect(jsonPath("$.pvpStatus").value("PvP"));
    }

    @Test
    void testGetPvpStatusById_NotFound() throws Exception {
        mockMvc.perform(get("/api/pvpStatuses/200"))
                .andExpect(status().isNotFound());
    }
}
