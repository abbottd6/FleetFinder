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

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = TestEnvironmentLoader.class, classes = SimpMessageTestConfig.class)
public class GroupStatusControllerIntegrationTest extends AbstractIntegrationTestDB {

    @Autowired
    private MockMvc mockMvc;

    //testing responseDtoes exist
    @Test
    void testGetAllGroupStatuses_Success_200() throws Exception {
        mockMvc.perform(get("/api/lookup/group-statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].groupStatusId").exists())
                .andExpect(jsonPath("$[0].groupStatus").exists());
    }

    //testing responseDto values
    @Test
    void testGetGroupStatusById_Success_200() throws Exception {
        mockMvc.perform(get("/api/lookup/group-statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupStatusId").value(1))
                .andExpect(jsonPath("$.groupStatus").value("Current/Live"));
    }

    @Test
    void testGetGroupStatusById_Failure_404() throws Exception {
        mockMvc.perform(get("/api/lookup/group-statuses/500"))
                .andExpect(status().isNotFound());
    }
}
