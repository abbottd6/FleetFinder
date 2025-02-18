package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PlanetMoonSystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.PlanetMoonSystemCachingServiceImpl;
import nl.altindag.log.LogCaptor;
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
    private PlanetMoonSystemRepository planetMoonSystemRepository;

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

    @Test
    void testConvertToDto_Success() {
        //given
        //planetary system for populating planet moon's parent system
        PlanetarySystem mockSystemEntity = new PlanetarySystem();
            mockSystemEntity.setSystemId(1);
            mockSystemEntity.setSystemName("Test System");

        //planet moon systems for conversion test
        PlanetMoonSystem mockPlanetEntity1 = new PlanetMoonSystem();
            mockPlanetEntity1.setPlanetId(1);
            mockPlanetEntity1.setPlanetName("Test Planet1");
            mockPlanetEntity1.setPlanetarySystem(mockSystemEntity);
        PlanetMoonSystem mockPlanetEntity2 = new PlanetMoonSystem();
            mockPlanetEntity2.setPlanetId(2);
            mockPlanetEntity2.setPlanetName("Test Planet2");
            mockPlanetEntity2.setPlanetarySystem(mockSystemEntity);

        //adding planet moons to parent system set
        mockSystemEntity.getPlanetMoonSystems().addAll(List.of(mockPlanetEntity1, mockPlanetEntity2));

        //when
        List<PlanetMoonSystemDto> result = List.of(planetMoonSystemService.convertToDto(mockPlanetEntity1),
                planetMoonSystemService.convertToDto(mockPlanetEntity2));

        //then
        assertAll("convert planet moon system entity to DTO assertion set:",
                () -> assertNotNull(result, "convert planet moon system entity to DTO should not return null"),
                () -> assertEquals(2, result.size(), "test planet moon convertToDto should produce" +
                        "2 elements"),
                () -> assertEquals(1, result.getFirst().getPlanetId(), "convert planet moon system" +
                        " entity produced DTO with incorrect Id"),
                () -> assertEquals("Test Planet1", result.getFirst().getPlanetName(), "convert " +
                        "planet moon system entity produced DTO with incorrect Planet Name"),
                () -> assertEquals("Test System", result.getFirst().getSystemName(), "covnert planet " +
                        "moon system entity produced DTO with incorrect parent system name"),
                () -> assertEquals(2, result.get(1).getPlanetId(), "convert planet moon system" +
                        " produced DTO with incorrect Id"),
                () -> assertEquals("Test Planet2", result.get(1).getPlanetName(), "convert planet moon" +
                        " system produced DTO with incorrect planet name"),
                () -> assertEquals("Test System", result.get(1).getSystemName(), "convert planet moon " +
                        "system produced DTO with incorrect parent system name"));
    }

    @Test
    void testConvertToDto_NullFailure() {
        //log captor for validating log message when entity is null
        LogCaptor logCaptor = LogCaptor.forClass(PlanetMoonSystemServiceImpl.class);

        //given
        //when a null entity is passed for conversion
        PlanetMoonSystemDto result = planetMoonSystemService.convertToDto(null);

        assertAll("convert planet moon system entity to DTO null failure assertion set:",
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Received null entity while converting PlanetMoonSystem to Dto " +
                            "for caching."))),
                () -> assertNull(result, "returned result should be null when null entity is passed"));
    }

    @Test
    void testConvertToDto_ID_NullFailure() {
        LogCaptor logCaptor = LogCaptor.forClass(PlanetMoonSystemServiceImpl.class);

        //given an entity with a null id
        PlanetMoonSystem mockEntity = new PlanetMoonSystem();
            mockEntity.setPlanetId(null);
            mockEntity.setPlanetName("Test Planet1");
            mockEntity.setPlanetarySystem(new PlanetarySystem());

        //when the entity is passed for conversion
        PlanetMoonSystemDto result = planetMoonSystemService.convertToDto(mockEntity);

        //then
        assertAll("convert planet moon system entity to DTO id = null assertion set:",
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Received null planet id while converting PlanetMoonSystem to Dto for caching."))),
                () -> assertNull(result, "returned result should be null when entity with null Id is passed"));
    }

    @Test
    void testConvertToDto_ID_ZeroFailure() {
        LogCaptor logCaptor = LogCaptor.forClass(PlanetMoonSystemServiceImpl.class);

        //given an entity with a null id
        PlanetMoonSystem mockEntity = new PlanetMoonSystem();
        mockEntity.setPlanetId(0);
        mockEntity.setPlanetName("Test Planet1");
        mockEntity.setPlanetarySystem(new PlanetarySystem());

        //when the entity is passed for conversion
        PlanetMoonSystemDto result = planetMoonSystemService.convertToDto(mockEntity);

        //then
        assertAll("convert planet moon system entity to DTO id = null assertion set:",
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Received null planet id while converting PlanetMoonSystem to Dto for caching."))),
                () -> assertNull(result, "returned result should be null when entity with null Id is passed"));
    }

    @Test
    void testConverToDto_NameFailure() {
        LogCaptor logCaptor = LogCaptor.forClass(PlanetMoonSystemServiceImpl.class);

        //given an entity with a null name
            PlanetMoonSystem mockEntity1 = new PlanetMoonSystem();
                mockEntity1.setPlanetId(1);
                mockEntity1.setPlanetName(null);
                mockEntity1.setPlanetarySystem(new PlanetarySystem());

        //and given an entity with an empty name
            PlanetMoonSystem mockEntity2 = new PlanetMoonSystem();
                mockEntity2.setPlanetId(2);
                mockEntity2.setPlanetName("");
                mockEntity2.setPlanetarySystem(new PlanetarySystem());

        //when converting these entities
        PlanetMoonSystemDto result1 = planetMoonSystemService.convertToDto(mockEntity1);
        PlanetMoonSystemDto result2 = planetMoonSystemService.convertToDto(mockEntity2);

        //counting the number of occurrences of the error message
        long logCount = logCaptor.getErrorLogs().stream()
                .filter(log -> log.contains("Received null planet name while converting " +
                        "PlanetMoonSystem to Dto for caching."))
                        .count();

        //then
        assertAll("convert planet moon system entity to DTO name failures assertion set:",
                () -> assertEquals(2, logCount),
                () -> assertNull(result1, "returned result should be null when entity with null name is passed"),
                () -> assertNull(result2, "returned result should be null when entity with null name is passed"));
    }

}