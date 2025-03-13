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
public class EnvironmentsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetAllGameEnvironments_Success_200() throws Exception {
        mockMvc.perform(get("/api/gameEnvironments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$[0].environmentId").exists())
                .andExpect(jsonPath("$[0].environmentType").exists());
    }

    @Test
    void testGetEnvironmentById_Success_200() throws Exception {
        mockMvc.perform(get("/api/gameEnvironments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.environmentId").value(1))
                .andExpect(jsonPath("$.environmentType").value("LIVE"));
    }

    @Test
    void testGetEnvironmentById_invalidId_404() throws Exception {
        mockMvc.perform(get("/api/environments/5000"))
                .andExpect(status().isNotFound());
    }
}
