package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PlanetarySystemServiceImplTest {

    @Mock
    private PlanetarySystemRepository planetarySystemRepository;

    @InjectMocks
    private PlanetarySystemServiceImpl planetarySystemService;

    @Test
    void testGetAllPlanetarySystems_Found() {
        //given
        PlanetarySystem mockPlanetarySystem1 = new PlanetarySystem();
        PlanetarySystem mockPlanetarySystem2 = new PlanetarySystem();
        List<PlanetarySystem> mockSystems = List.of(mockPlanetarySystem1, mockPlanetarySystem2);
        when(planetarySystemRepository.findAll()).thenReturn(mockSystems);

        //when
        List<PlanetarySystemDto> result = planetarySystemService.getAllPlanetarySystems();

        //then
        assertAll("getAllPlanetarySystems mock entities assertion set:",
                () -> assertNotNull(result, "getAllPlanetarySystems should not return null"),
                () -> assertEquals(2, result.size(),
                        "getAllPlanetarySystems should return 2 mock entities"),
                () -> verify(planetarySystemRepository, times(1)).findAll());
    }

    @Test
    void testGetAllPlanetarySystems_NotFound() {
        //given
        when(planetarySystemRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<PlanetarySystemDto> result = planetarySystemService.getAllPlanetarySystems();

        //then
        assertAll("getAllPlanetarySystems = empty, assertion set: ",
                () -> assertNotNull(result, "getAllPlanetarySystems should be empty, not null"),
                () -> assertTrue(result.isEmpty(), "getAllPlanetarySystems returned " + result + " when " +
                        "it should have returned empty"),
                () -> verify(planetarySystemRepository, times(1)).findAll());
    }

    @Test
    void testGetPlanetarySystemById_Found() {
        //given
        PlanetarySystem mockPlanetarySystem1 = new PlanetarySystem();
        when(planetarySystemRepository.findById(1)).thenReturn(Optional.of(mockPlanetarySystem1));

        //when
        PlanetarySystemDto result = planetarySystemService.getPlanetarySystemById(1);

        //then
        assertAll("getPlanetarySystemById = found assertion set:",
                () -> assertNotNull(result, "getPlanetarySystemById = found should not return null"),
                () -> assertDoesNotThrow(() -> planetarySystemService.getPlanetarySystemById(1),
                        "getPlanetarySystemById should not throw an exception when Id is found"),
                () -> verify(planetarySystemRepository, times(2)).findById(1));
    }

    @Test
    void testGetPlanetarySystemById_NotFound() {
        //given
        when(planetarySystemRepository.findById(1)).thenReturn(Optional.empty());

        //when planetarySystemRepository does not contain entity with given Id

        //then
        assertThrows(ResourceNotFoundException.class, () -> planetarySystemService.getPlanetarySystemById(1),
                "getPlanetarySystemById should throw an exception when Id is not found");
    }
}