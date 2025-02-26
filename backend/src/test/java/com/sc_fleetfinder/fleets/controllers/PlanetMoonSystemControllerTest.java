package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.PlanetMoonSystemService;
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

@WebMvcTest(controllers = PlanetMoonSystemController.class)
class PlanetMoonSystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlanetMoonSystemService planetMoonSystemService;

    private List<PlanetMoonSystemDto> mockPlanetDtos;

    @BeforeEach
    void setUp() {
        PlanetMoonSystemDto mockPlanetDto1 = new PlanetMoonSystemDto();
        mockPlanetDto1.setPlanetId(1);
        mockPlanetDto1.setPlanetName("Stanton I");
        PlanetMoonSystemDto mockPlanetDto2 = new PlanetMoonSystemDto();
        mockPlanetDto2.setPlanetId(2);
        mockPlanetDto2.setPlanetName("Pyro I");

        mockPlanetDtos = Arrays.asList(mockPlanetDto1, mockPlanetDto2);
    }

    @Test
    void testGetAllPlanetMoonSystems_FoundList() throws Exception {
        when(planetMoonSystemService.getAllPlanetMoonSystems()).thenReturn(mockPlanetDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/planetMoonSystems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].planetId").value(1))
                .andExpect(jsonPath("$[0].planetName").value("Stanton I"))
                .andExpect(jsonPath("$[1].planetId").value(2))
                .andExpect(jsonPath("$[1].planetName").value("Pyro I"));
    }

    @Test
    void testGetAllPlanetMoonSystems_NotFound() throws Exception {
        when(planetMoonSystemService.getAllPlanetMoonSystems())
                .thenThrow(new ResourceNotFoundException("Unable to access data for planet moon systems"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/planetMoonSystems"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPlanetMoonSystemById_Found() throws Exception {
        when(planetMoonSystemService.getPlanetMoonSystemById(1)).thenReturn(mockPlanetDtos.getFirst());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/planetMoonSystems/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planetId").value(1))
                .andExpect(jsonPath("$.planetName").value("Stanton I"));
    }

    @Test
    void testGetPlanetMoonSystemById_NotFound() throws Exception {
        when(planetMoonSystemService.getPlanetMoonSystemById(33)).thenThrow(new ResourceNotFoundException(33));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/planetMoonSystems/33"))
                .andExpect(status().isNotFound());
    }
}