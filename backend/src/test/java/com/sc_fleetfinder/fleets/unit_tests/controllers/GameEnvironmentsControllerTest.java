package com.sc_fleetfinder.fleets.unit_tests.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.controllers.GameEnvironmentsController;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GameEnvironmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GameEnvironmentsController.class)
class GameEnvironmentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameEnvironmentService gameEnvironmentService;

    private List<GameEnvironmentDto> mockEnvironments;

    @BeforeEach
    void setUp() {
        GameEnvironmentDto env1 = new GameEnvironmentDto();
            env1.setEnvironmentId(1);
            env1.setEnvironmentType("LIVE");
        GameEnvironmentDto env2 = new GameEnvironmentDto();
            env2.setEnvironmentId(2);
            env2.setEnvironmentType("PTU");

        mockEnvironments = Arrays.asList(env1, env2);
    }

    @Test
    void testGetAllGameEnvironments_FoundList() throws Exception {
        when(gameEnvironmentService.getAllEnvironments()).thenReturn(mockEnvironments);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/lookup/game-environments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].environmentId").value(1))
                .andExpect(jsonPath("$[0].environmentType").value("LIVE"))
                .andExpect(jsonPath("[1].environmentId").value(2))
                .andExpect(jsonPath("$[1].environmentType").value("PTU"));
    }

    @Test
    void testGetAllGameEnvironments_NotFound() throws Exception {
        when(gameEnvironmentService.getAllEnvironments())
                .thenThrow(new ResourceNotFoundException("Unable to access game environments data"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/lookup/game-environments"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetGameEnvironmentById_Found() throws Exception{
        when(gameEnvironmentService.getEnvironmentById(1)).thenReturn(mockEnvironments.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/lookup/game-environments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.environmentId").value(1))
                .andExpect(jsonPath("$.environmentType").value("LIVE"));
    }

    @Test
    void testGetGameEnvironmentById_NotFound() throws Exception{
        when(gameEnvironmentService.getEnvironmentById(33)).thenThrow(new ResourceNotFoundException(33));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/lookup/game-environments/33"))
                .andExpect(status().isNotFound());
    }
}