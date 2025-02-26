package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.PlanetarySystemService;
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

@WebMvcTest(controllers = PlanetarySystemController.class)
class PlanetarySystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlanetarySystemService planetarySystemService;

    private List<PlanetarySystemDto> mockSystems;

    @BeforeEach
    void setUp() {
        PlanetarySystemDto mockSystemDto1 =  new PlanetarySystemDto();
        mockSystemDto1.setSystemId(1);
        mockSystemDto1.setSystemName("Stanton");
        PlanetarySystemDto mockSystemDto2 =  new PlanetarySystemDto();
        mockSystemDto2.setSystemId(2);
        mockSystemDto2.setSystemName("Pyro");

        mockSystems = Arrays.asList(mockSystemDto1, mockSystemDto2);
    }

    @Test
    void testGetAllPlanetarySystems_FoundList() throws Exception {
        when(planetarySystemService.getAllPlanetarySystems()).thenReturn(mockSystems);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/planetarySystems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].systemId").value(1))
                .andExpect(jsonPath("$[0].systemName").value("Stanton"))
                .andExpect(jsonPath("$[1].systemId").value(2))
                .andExpect(jsonPath("$[1].systemName").value("Pyro"));
    }

    @Test
    void testGetAllPlanetarySystems_NotFound() throws Exception {
        when(planetarySystemService.getAllPlanetarySystems())
        .thenThrow(new ResourceNotFoundException("Unable to access data for planetary systems"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/planetarySystems"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPlanetarySystemById() throws Exception {
        when(planetarySystemService.getPlanetarySystemById(1)).thenReturn(mockSystems.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/planetarySystems/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.systemId").value(1))
                .andExpect(jsonPath("$.systemName").value("Stanton"));
    }

    @Test
    void testGetPlanetarySystemByIdNotFound() throws Exception {
        when(planetarySystemService.getPlanetarySystemById(33)).thenThrow(new ResourceNotFoundException(33));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/planetarySystems/33"))
                .andExpect(status().isNotFound());
    }
}