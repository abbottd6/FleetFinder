package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.PlanetarySystemCachingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PlanetarySystemServiceImplTest {

    @Mock
    private PlanetarySystemCachingServiceImpl planetarySystemCachingService;

    @InjectMocks
    private PlanetarySystemServiceImpl planetarySystemService;

    @Test
    void testGetAllPlanetarySystems_Found() {
        //given
        PlanetarySystemDto mockDto1 = new PlanetarySystemDto();
        PlanetarySystemDto mockDto2 = new PlanetarySystemDto();
        List<PlanetarySystemDto> mockDtoes = List.of(mockDto1, mockDto2);
        when(planetarySystemCachingService.cacheAllPlanetarySystems()).thenReturn(mockDtoes);

        //when
        List<PlanetarySystemDto> result = planetarySystemService.getAllPlanetarySystems();

        //then
        assertAll("getAllPlanetarySystems mock entities assertion set:",
                () -> assertNotNull(result, "getAllPlanetarySystems should not return null"),
                () -> assertEquals(2, result.size(),
                        "getAllPlanetarySystems should return 2 mock entities"),
                () -> verify(planetarySystemCachingService, times(1)).cacheAllPlanetarySystems());
    }

    @Test
    void testGetAllPlanetarySystems_NotFound() {
        //given: planetary systems repo is empty

        //telling test to throw an exception when it tries to cache an emtpy repo
        when(planetarySystemCachingService.cacheAllPlanetarySystems()).thenThrow(ResourceNotFoundException.class);

        //when
        //then
        assertAll("getAllPlanetarySystems = empty assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> planetarySystemService.getAllPlanetarySystems()),
                () -> verify(planetarySystemCachingService, times(1)).cacheAllPlanetarySystems());
    }

    @Test
    void testGetPlanetarySystemById_Found() {
        //given
        PlanetarySystemDto mockDto1 = new PlanetarySystemDto();
            mockDto1.setSystemId(1);
            mockDto1.setSystemName("System1");
        PlanetarySystemDto mockDto2 = new PlanetarySystemDto();
            mockDto2.setSystemId(2);
            mockDto2.setSystemName("System2");
        List<PlanetarySystemDto> mockDtoes = List.of(mockDto1, mockDto2);
        when(planetarySystemCachingService.cacheAllPlanetarySystems()).thenReturn(mockDtoes);

        //when
        PlanetarySystemDto result = planetarySystemService.getPlanetarySystemById(1);

        //then
        assertAll("getPlanetarySystemById = found assertion set:",
                () -> assertNotNull(result, "getPlanetarySystemById = found should not return null"),
                () -> assertDoesNotThrow(() -> planetarySystemService.getPlanetarySystemById(1),
                        "getPlanetarySystemById should not throw an exception when Id is found"),
                () -> verify(planetarySystemCachingService, times(2)).cacheAllPlanetarySystems());
    }

    @Test
    void testGetPlanetarySystemById_NotFound() {
        //given: planetarySystemRepository does not contain the given id

        //when
        //then
        assertAll("getPlanetarySystemById = not found assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> planetarySystemService.getPlanetarySystemById(1),
                "getPlanetarySystemById should throw an exception when Id is not found"),
                () -> verify(planetarySystemCachingService, times(1)).cacheAllPlanetarySystems());
    }
}