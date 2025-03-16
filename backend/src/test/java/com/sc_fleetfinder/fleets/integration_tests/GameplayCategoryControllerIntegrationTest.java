package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.services.CRUD_services.GameplayCategoryService;
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
public class GameplayCategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GameplayCategoryService gameplayCategoryService;

    //troubleshooting failed tests (problem was field names did not match responseDto fields)
    @Test
    void debugTestData() {
        System.out.println("gameplay categories in test DB: " + gameplayCategoryService.getAllCategories());
    }

    //testing responseDtoes exist
    @Test
    void testGetAllGameplayCategories_Success_200() throws Exception {
        mockMvc.perform(get("/api/lookup/gameplay-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(12))
                .andExpect(jsonPath("$[0].gameplayCategoryId").exists())
                .andExpect(jsonPath("$[0].gameplayCategoryName").exists());
    }

    //testing responseDto values
    @Test
    void testGetGameplayCategoryById_Success_200() throws Exception {
        mockMvc.perform(get("/api/lookup/gameplay-categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameplayCategoryId").value(1))
                .andExpect(jsonPath("$.gameplayCategoryName").value("Ship Combat"));
    }

    @Test
    void testGetGameplayCategoryById_invalidId_404() throws Exception {
        mockMvc.perform(get("/api/lookup/gameplay-categories/500"))
                .andExpect(status().isNotFound());
    }
}
