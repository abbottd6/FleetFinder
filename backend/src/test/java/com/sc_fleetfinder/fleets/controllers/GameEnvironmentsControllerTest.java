package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.GameEnvironmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameEnvironmentsController.class)
class GameEnvironmentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameEnvironmentService gameEnvironmentService;

    private ArrayList<GameEnvironmentDto> mockEnvironments;

    @BeforeEach
    void setUp() {
        GameEnvironmentDto env1 = new GameEnvironmentDto();
            env1.setEnvironmentId(1);
            env1.setEnvironmentType("PU");
        GameEnvironmentDto env2 = new GameEnvironmentDto();
            env2.setEnvironmentId(2);
            env2.setEnvironmentType("AC");

        mockEnvironments = new ArrayList<>(Arrays.asList(env1, env2));
    }

    @Test
    void getGameEnvironments_FoundList() throws Exception {
        when(gameEnvironmentService.getAllEnvironments()).thenReturn(mockEnvironments);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameEnvironments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].environmentId").value(1))
                .andExpect(jsonPath("$[0].environmentType").value("PU"))
                .andExpect(jsonPath("[1].environmentId").value(2))
                .andExpect(jsonPath("$[1].environmentType").value("AC"));
    }

    @Test
    void getGameEnvironmentById_Found() throws Exception{
        when(gameEnvironmentService.getEnvironmentById(1)).thenReturn(mockEnvironments.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameEnvironments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.environmentId").value(1))
                .andExpect(jsonPath("$.environmentType").value("PU"));
    }

    @Test
    void getGameEnvironmentById_NotFound() throws Exception{
        when(gameEnvironmentService.getEnvironmentById(33)).thenThrow(new ResourceNotFoundException(33));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/gameEnvironments/33"))
                .andExpect(status().isNotFound());
    }
}