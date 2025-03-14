package com.sc_fleetfinder.fleets.integration_tests;

import com.sc_fleetfinder.fleets.config.TestEnvironmentLoader;
import com.sc_fleetfinder.fleets.services.CRUD_services.ServerRegionService;
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
public class ServerRegionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    //testing that the correct number of server regions exist
    @Test
    void testGetAllServerRegions_Success() throws Exception {
        mockMvc.perform(get("/api/serverRegions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].serverId").exists())
                .andExpect(jsonPath("$[0].servername").exists());
    }

    //testing the server region attribute values
    @Test
    void testGetServerRegionById_Success() throws Exception {
        mockMvc.perform(get("/api/serverRegions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverId").value(1))
                .andExpect(jsonPath("$.servername").value("USA"));
    }

    @Test
    void testGetServerRegionById_NotFound() throws Exception {
        mockMvc.perform(get("/api/serverRegions/200"))
                .andExpect(status().isNotFound());
    }
}
