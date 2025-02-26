package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.PlanetMoonSystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.PlanetMoonSystemConversionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanetMoonSystemCachingServiceImplTest {

    @Mock
    private PlanetMoonSystemRepository planetMoonSystemRepository;

    @Mock
    private PlanetMoonSystemConversionServiceImpl planetMoonSystemConversionService;

    @InjectMocks
    private PlanetMoonSystemCachingServiceImpl planetMoonSystemCachingService;

    @Test
    void testCacheAllLegalities_Found() {
        //given mock entities for find all
        PlanetarySystem mockSystemEntity = new PlanetarySystem();
            mockSystemEntity.setSystemId(1);
            mockSystemEntity.setSystemName("System1");
        PlanetMoonSystem mockEntity1 = new PlanetMoonSystem();
            mockEntity1.setPlanetId(1);
            mockEntity1.setPlanetName("Planet1");
            mockEntity1.setPlanetarySystem(mockSystemEntity);
        PlanetMoonSystem mockEntity2 = new PlanetMoonSystem();
            mockEntity2.setPlanetId(2);
            mockEntity2.setPlanetName("Planet2");
            mockEntity2.setPlanetarySystem(mockSystemEntity);
        List<PlanetMoonSystem> mockEntities = List.of(mockEntity1, mockEntity2);
        when(planetMoonSystemRepository.findAll()).thenReturn(mockEntities);

        //mock dto's to return from conversion in cacheAll
        PlanetMoonSystemDto mockDto1 = new PlanetMoonSystemDto();
            mockDto1.setPlanetId(1);
            mockDto1.setPlanetName("Planet1");
            mockDto1.setSystemName("System1");
        PlanetMoonSystemDto mockDto2 = new PlanetMoonSystemDto();
            mockDto2.setPlanetId(2);
            mockDto2.setPlanetName("Planet2");
            mockDto2.setSystemName("System1");
        when(planetMoonSystemConversionService.convertToDto(mockEntity1)).thenReturn(mockDto1);
        when(planetMoonSystemConversionService.convertToDto(mockEntity2)).thenReturn(mockDto2);

        //when
        List<PlanetMoonSystemDto> result = planetMoonSystemCachingService.cacheAllPlanetMoonSystems();

        //then
        assertAll("cacheAllPlanetMoonSystems = found assertion set: ",
                () -> assertNotNull(result, "cacheAllPlanetMoonSystems should not return null here, " +
                        "2 dtos expected"),
                () -> assertEquals(2, result.size(), "cacheAllPlanetMoonSystems should have returned" +
                        " 2 mock Dtos"),
                () -> assertEquals(1, result.getFirst().getPlanetId(), "cacheAllPlanetMoonSystems " +
                        "produced a dto with an incorrect Id"),
                () -> assertEquals("Planet1", result.getFirst().getPlanetName(),
                        "cacheAllPlanetMoonSystems produced a dto with an incorrect planet name"),
                () -> assertEquals("System1", result.getFirst().getSystemName(),
                        "cacheAllPlanetMoonSystems produced a dto with an incorrect SYSTEM name, for the " +
                                "parent star system"),
                () -> assertEquals(2, result.get(1).getPlanetId(), "cacheAllPlanetMoonSystems " +
                        "produced a dto with an incorrect id"),
                () -> assertEquals("Planet2", result.get(1).getPlanetName(),
                        "cacheAllPlanetMoonSystems produced a dto with an incorrect planet name"),
                () -> assertEquals("System1", result.get(1).getSystemName(),
                        "cacheAllPlanetMoonSystems produced a dto with an incorrect SYSTEM name, for " +
                                "the parent star system"),
                () -> verify(planetMoonSystemRepository, times(1)).findAll());
    }

    @Test
    void testCacheAllPlanetMoonSystems_NotFound() {
        //given: planet moon systems repo is empty

        //when
        //then
        assertAll("cachingAllPlanetMoonSystems = empty assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> planetMoonSystemCachingService.cacheAllPlanetMoonSystems()),
                () -> verify(planetMoonSystemRepository, times(1)).findAll());
    }
}