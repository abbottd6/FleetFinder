package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.PlanetarySystemConversionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanetarySystemCachingServiceImplTest {

    @Mock
    private PlanetarySystemRepository planetarySystemRepository;

    @Mock
    private PlanetarySystemConversionServiceImpl planetarySystemConversionService;

    @InjectMocks
    private PlanetarySystemCachingServiceImpl planetarySystemCachingService;

    @Test
    void testCacheAllPlanetarySystems_Found() {
        //given
        //mock entities for find all
        PlanetarySystem mockEntity1 = new PlanetarySystem();
            mockEntity1.setSystemId(1);
            mockEntity1.setSystemName("System1");
            mockEntity1.setPlanetMoonSystems(Set.of(new PlanetMoonSystem()));
        PlanetarySystem mockEntity2 = new PlanetarySystem();
            mockEntity2.setSystemId(2);
            mockEntity2.setSystemName("System2");
            mockEntity2.setPlanetMoonSystems(Set.of(new PlanetMoonSystem()));
        List<PlanetarySystem> mockEntities = List.of(mockEntity1, mockEntity2);
        when(planetarySystemRepository.findAll()).thenReturn(mockEntities);

        //mock dtoes to return from conversion in cacheAll
        PlanetarySystemDto mockDto1 = new PlanetarySystemDto();
            mockDto1.setSystemId(1);
            mockDto1.setSystemName("System1");
        PlanetarySystemDto mockDto2 = new PlanetarySystemDto();
            mockDto2.setSystemId(2);
            mockDto2.setSystemName("System2");
        when(planetarySystemConversionService.convertToDto(mockEntity1)).thenReturn(mockDto1);
        when(planetarySystemConversionService.convertToDto(mockEntity2)).thenReturn(mockDto2);

        //when
        List<PlanetarySystemDto> result = planetarySystemCachingService.cacheAllPlanetarySystems();

        //then
        assertAll("cacheAllPlanetarySystems mock entities assertion set: ",
                () -> assertNotNull(result, "cacheAllPlanetarySystems should not return null"),
                () -> assertEquals(2, result.size(), "cacheAllPlanetary systems should return a list " +
                        "of 2 mock Dtos"),
                () -> assertEquals(1, result.getFirst().getSystemId(), "cacheAllPlanetarySystems " +
                        "produced a dto with an incorrect id"),
                () -> assertEquals("System1", result.getFirst().getSystemName(),
                        "cacheAllPlanetarySystems produced a dto with an incorrect SystemName"),
                () -> assertEquals(2, result.get(1).getSystemId(), "cacheAllPlanetarySystmes " +
                        "produced a dto with an incorrect id"),
                () -> assertEquals("System2", result.get(1).getSystemName(), "cacheAllPlanetarySystems " +
                        "produced a dto with an incorrect SystemName"),
                () -> verify(planetarySystemRepository, times(1)).findAll());
    }

    @Test
    void testCacheAllPlanetarySystems_NotFound() {
        //given: planetary systems repo is empty

        //when
        //then
        assertAll("cacheAllPlanetarySystems = empty assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class,
                        () -> planetarySystemCachingService.cacheAllPlanetarySystems()),
                () -> verify(planetarySystemRepository, times(1)).findAll());
    }
}