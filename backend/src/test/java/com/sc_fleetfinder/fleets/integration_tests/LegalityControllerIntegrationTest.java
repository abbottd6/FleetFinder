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
public class LegalityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    //testing responseDtoes exist
    @Test
    void testGetAllLegalities_Success_200() throws Exception {
        mockMvc.perform(get("/api/legalities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].legalityId").exists())
                .andExpect(jsonPath("$[0].legalityStatus").exists());
    }

    //testing responseDto values
    @Test
    void testGetLegalityById_Success_200() throws Exception {
        mockMvc.perform(get("/api/legalities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalityId").value(1))
                .andExpect(jsonPath("$.legalityStatus").value("Lawful"));
    }

    @Test
    void testGetLegalityById_Failure_404() throws Exception {
        mockMvc.perform(get("/api/legalities/500"))
                .andExpect(status().isNotFound());
    }
}
