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
public class GameplaySubcategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    //testing responseDtoes exist
    @Test
    void testGetAllSubcategories_Success_200() throws Exception {
        mockMvc.perform(get("/api/lookup/gameplay-subcategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(41))
                .andExpect(jsonPath("$[0].subcategoryId").exists())
                .andExpect(jsonPath("$[0].subcategoryName").exists())
                .andExpect(jsonPath("$[0].gameplayCategoryName").exists());
    }

    //testing responseDto values
    @Test
    void testGetSubcategoryById_Success_200() throws Exception {
        mockMvc.perform(get("/api/lookup/gameplay-subcategories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subcategoryId").value(1))
                .andExpect(jsonPath("$.subcategoryName").value("Bounty Hunting PVP"))
                .andExpect(jsonPath("$.gameplayCategoryName").value("Ship Combat"));
    }

    @Test
    void testGetSubcategoryById_invalidId_404() throws Exception {
        mockMvc.perform(get("/api/lookup/gameplay-subcategories/500"))
                .andExpect(status().isNotFound());
    }
}
