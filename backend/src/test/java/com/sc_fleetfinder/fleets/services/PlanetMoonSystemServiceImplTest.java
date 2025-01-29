package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PlanetMoonSystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanetMoonSystemServiceImplTest {

    @Mock
    private PlanetMoonSystemRepository planetMoonSystemRepository;

    @InjectMocks
    private PlanetMoonSystemServiceImpl planetMoonSystemService;

    @Test
    void testGetAllPlanetMoonSystems_Found() {
        //given
        PlanetMoonSystem mockPlanet1 = new PlanetMoonSystem();
        PlanetMoonSystem mockPlanet2 = new PlanetMoonSystem();
        List<PlanetMoonSystem> mockPlanets = List.of(mockPlanet1, mockPlanet2);
        when(planetMoonSystemRepository.findAll()).thenReturn(mockPlanets);

        //when
        List<PlanetMoonSystemDto> result = planetMoonSystemService.getAllPlanetMoonSystems();

        //then
        assertAll("getAllPlanetMoonSystems mock entities assertion set: ",
                () -> assertNotNull(result, "getAllPlanetMoonSystems should not return null"),
                () -> assertEquals(2, result.size(), "getAllPlanetMoonSystems should return 2 " +
                        "mock entities"),
                () -> verify(planetMoonSystemRepository, times(1)).findAll());
    }

    @Test
    void testGetAllPlanetMoonSystems_NotFound() {
        //given
        when(planetMoonSystemRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<PlanetMoonSystemDto> result = planetMoonSystemService.getAllPlanetMoonSystems();

        //then
        assertAll("getAllPlanetMoonSystems = empty, assertion set: ",
                () -> assertNotNull(result, "getAllPlanetMoonSystems should be empty, not null"),
                () -> assertTrue(result.isEmpty(), "getAllPlanetMoonSystems returned " + result +
                        " when it should have returned empty"),
                () -> verify(planetMoonSystemRepository, times (1)).findAll());
    }

    @Test
    void testGetPlanetMoonSystemById_Found() {
        //given
        PlanetMoonSystem mockSystem = new PlanetMoonSystem();
        when(planetMoonSystemRepository.findById(1)).thenReturn(Optional.of(mockSystem));

        //when
        PlanetMoonSystemDto result = planetMoonSystemService.getPlanetMoonSystemById(1);

        //then
        assertAll("getPlanetMoonSystemById = found assertion set: ",
                () -> assertNotNull(result, "getPlanetMoonSystemById should not return null when Id is found"),
                () -> assertDoesNotThrow(() -> planetMoonSystemService.getPlanetMoonSystemById(1),
                        "getPlanetMoonSystemById should not throw an exception when Id is found"),
                () -> verify(planetMoonSystemRepository, times(2)).findById(1));
    }

    @Test
    void testGetPlanetMoonSystemById_NotFound() {
        //given
        when(planetMoonSystemRepository.findById(1)).thenReturn(Optional.empty());

        //when planetMoonSystemRepository does not contain entity with given Id

        //then
        assertThrows(ResourceNotFoundException.class, () -> planetMoonSystemService.getPlanetMoonSystemById(1),
                "getPlanetMoonSystemById should throw an exception when given Id is not found");
    }
}