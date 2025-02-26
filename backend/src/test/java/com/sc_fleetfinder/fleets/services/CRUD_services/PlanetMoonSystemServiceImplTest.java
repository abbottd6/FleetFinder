package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.PlanetMoonSystemCachingServiceImpl;
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
class PlanetMoonSystemServiceImplTest {

    @Mock
    private PlanetMoonSystemCachingServiceImpl planetMoonSystemCachingService;

    @InjectMocks
    private PlanetMoonSystemServiceImpl planetMoonSystemService;

    @Test
    void testGetAllPlanetMoonSystems_Found() {
        //given
        PlanetMoonSystemDto mockPlanetDto1 = new PlanetMoonSystemDto();
        PlanetMoonSystemDto mockPlanetDto2 = new PlanetMoonSystemDto();
        List<PlanetMoonSystemDto> mockPlanetDtoes = List.of(mockPlanetDto1, mockPlanetDto2);
        when(planetMoonSystemCachingService.cacheAllPlanetMoonSystems()).thenReturn(mockPlanetDtoes);

        //when
        List<PlanetMoonSystemDto> result = planetMoonSystemService.getAllPlanetMoonSystems();

        //then
        assertAll("getAllPlanetMoonSystems mock entities assertion set: ",
                () -> assertNotNull(result, "getAllPlanetMoonSystems should not return null"),
                () -> assertEquals(2, result.size(), "getAllPlanetMoonSystems should return 2 " +
                        "mock entities"),
                () -> verify(planetMoonSystemCachingService, times(1)).cacheAllPlanetMoonSystems());
    }

    @Test
    void testGetAllPlanetMoonSystems_NotFound() {
        //given planet moon systems repo is empty

        //telling test to throw an exception when it tries to cache an empty repo
        when(planetMoonSystemCachingService.cacheAllPlanetMoonSystems()).thenThrow(ResourceNotFoundException.class);

        //when
        //then
        assertAll("getAllPlanetMoonSystems = empty, assertion set: ",
                //verifying that the exception propagates from the caching service
                () -> assertThrows(ResourceNotFoundException.class, () -> planetMoonSystemService.getAllPlanetMoonSystems()),
                () -> verify(planetMoonSystemCachingService, times (1)).cacheAllPlanetMoonSystems());
    }

    @Test
    void testGetPlanetMoonSystemById_Found() {
        //given
        PlanetMoonSystemDto mockPlanetDto1 = new PlanetMoonSystemDto();
            mockPlanetDto1.setPlanetId(1);
            mockPlanetDto1.setPlanetName("Test Planet1");
            mockPlanetDto1.setSystemName("Test System");
        PlanetMoonSystemDto mockPlanetDto2 = new PlanetMoonSystemDto();
            mockPlanetDto2.setPlanetId(2);
            mockPlanetDto2.setPlanetName("Test Planet1");
            mockPlanetDto2.setSystemName("Test System");
        List<PlanetMoonSystemDto> mockDtoes = List.of(mockPlanetDto1, mockPlanetDto2);
        when(planetMoonSystemCachingService.cacheAllPlanetMoonSystems()).thenReturn(mockDtoes);

        //when
        PlanetMoonSystemDto result = planetMoonSystemService.getPlanetMoonSystemById(1);

        //then
        assertAll("getPlanetMoonSystemById = found assertion set: ",
                () -> assertNotNull(result, "getPlanetMoonSystemById should not return null when Id is found"),
                () -> assertDoesNotThrow(() -> planetMoonSystemService.getPlanetMoonSystemById(1),
                        "getPlanetMoonSystemById should not throw an exception when Id is found"),
                () -> verify(planetMoonSystemCachingService, times(2)).cacheAllPlanetMoonSystems());
    }

    @Test
    void testGetPlanetMoonSystemById_NotFound() {
        //given: planet moon system repository does not contain planet with given id


        //when
        //then
        assertAll("getPlanetMoonSystemById = not found assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> planetMoonSystemService.getPlanetMoonSystemById(1),
                "getPlanetMoonSystemById should throw an exception when given Id is not found"),
                () -> verify(planetMoonSystemCachingService, times(1)).cacheAllPlanetMoonSystems());
    }
}