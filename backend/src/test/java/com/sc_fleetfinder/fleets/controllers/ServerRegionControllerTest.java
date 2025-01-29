package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.services.ServerRegionService;
import org.junit.jupiter.api.Test;

import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = ServerRegionController.class)
class ServerRegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServerRegionService serverRegionService;

    private List<ServerRegionDto> mockServerRegions;

    @BeforeEach
    void setUp() {
        ServerRegionDto mockServerDto1 = new ServerRegionDto();
        mockServerDto1.setServerId(1);
        mockServerDto1.setServername("USA");
        ServerRegionDto mockServerDto2 = new ServerRegionDto();
        mockServerDto2.setServerId(2);
        mockServerDto2.setServername("AUS");

        mockServerRegions = Arrays.asList(mockServerDto1, mockServerDto2);
    }

    @Test
    void testGetAllServerRegions_FoundList() throws Exception {
        when(serverRegionService.getAllServerRegions()).thenReturn(mockServerRegions);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/serverRegions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serverId").value(1))
                .andExpect(jsonPath("$[0].servername").value("USA"))
                .andExpect(jsonPath("$[1].serverId").value(2))
                .andExpect(jsonPath("$[1].servername").value("AUS"));
    }

    @Test
    void testGetAllServerRegions_NotFound() throws Exception {
        when(serverRegionService.getAllServerRegions())
                .thenThrow(new ResourceNotFoundException("Unable to access server region data"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/serverRegions"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetServerRegionById_Found() throws Exception {
        when(serverRegionService.getServerRegionById(1)).thenReturn(mockServerRegions.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/serverRegions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverId").value(1))
                .andExpect(jsonPath("$.servername").value("USA"));
    }

    @Test
    void testGetServerRegionById_NotFound() throws Exception {
        when(serverRegionService.getServerRegionById(33)).thenThrow(new ResourceNotFoundException(33));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/serverRegions/33"))
                .andExpect(status().isNotFound());
    }
}