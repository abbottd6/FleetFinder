package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.PlanetMoonSystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanetMoonSystemConversionServiceImplTest {

    @InjectMocks
    private PlanetMoonSystemConversionServiceImpl planetMoonSystemConversionService;

    @Mock
    private PlanetMoonSystemRepository planetMoonSystemRepository;

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
        List<PlanetMoonSystemDto> result = List.of(planetMoonSystemConversionService.convertToDto(mockPlanetEntity1),
                planetMoonSystemConversionService.convertToDto(mockPlanetEntity2));

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
        LogCaptor logCaptor = LogCaptor.forClass(PlanetMoonSystemConversionServiceImpl.class);

        //given
        //when a null entity is passed for conversion
        PlanetMoonSystemDto result = planetMoonSystemConversionService.convertToDto(null);

        assertAll("convert planet moon system entity to DTO null failure assertion set:",
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Received null entity while converting PlanetMoonSystem to Dto " +
                                "for caching."))),
                () -> assertNull(result, "returned result should be null when null entity is passed"));
    }

    @Test
    void testConvertToDto_ID_NullFailure() {
        LogCaptor logCaptor = LogCaptor.forClass(PlanetMoonSystemConversionServiceImpl.class);

        //given an entity with a null id
        PlanetMoonSystem mockEntity = new PlanetMoonSystem();
        mockEntity.setPlanetId(null);
        mockEntity.setPlanetName("Test Planet1");
        mockEntity.setPlanetarySystem(new PlanetarySystem());

        //when the entity is passed for conversion
        PlanetMoonSystemDto result = planetMoonSystemConversionService.convertToDto(mockEntity);

        //then
        assertAll("convert planet moon system entity to DTO id = null assertion set:",
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Received null planet id while converting PlanetMoonSystem to Dto for caching."))),
                () -> assertNull(result, "returned result should be null when entity with null Id is passed"));
    }

    @Test
    void testConvertToDto_ID_ZeroFailure() {
        LogCaptor logCaptor = LogCaptor.forClass(PlanetMoonSystemConversionServiceImpl.class);

        //given an entity with a null id
        PlanetMoonSystem mockEntity = new PlanetMoonSystem();
        mockEntity.setPlanetId(0);
        mockEntity.setPlanetName("Test Planet1");
        mockEntity.setPlanetarySystem(new PlanetarySystem());

        //when the entity is passed for conversion
        PlanetMoonSystemDto result = planetMoonSystemConversionService.convertToDto(mockEntity);

        //then
        assertAll("convert planet moon system entity to DTO id = null assertion set:",
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Received null planet id while converting PlanetMoonSystem to Dto for caching."))),
                () -> assertNull(result, "returned result should be null when entity with null Id is passed"));
    }

    @Test
    void testConvertToDto_NameFailure() {
        LogCaptor logCaptor = LogCaptor.forClass(PlanetMoonSystemConversionServiceImpl.class);

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
        PlanetMoonSystemDto result1 = planetMoonSystemConversionService.convertToDto(mockEntity1);
        PlanetMoonSystemDto result2 = planetMoonSystemConversionService.convertToDto(mockEntity2);

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

    @Test
    void testConvertToEntity_Found() {
        //given
        //mock parent system for planet
        PlanetarySystem mockSystemEntity = new PlanetarySystem();
        mockSystemEntity.setSystemId(1);
        mockSystemEntity.setSystemName("Test System1");

        //mock entity version of dto to convert
        PlanetMoonSystem mockEntity1 = new PlanetMoonSystem();
        mockEntity1.setPlanetId(1);
        mockEntity1.setPlanetName("Test Planet1");
        mockEntity1.setPlanetarySystem(mockSystemEntity);

        //set containing planet to add to parent system's set of planets
        Set<PlanetMoonSystem> mockPlanets = Set.of(mockEntity1);
        mockSystemEntity.setPlanetMoonSystems(mockPlanets);

        //mock planet dto to convert
        PlanetMoonSystemDto mockDto = new PlanetMoonSystemDto();
        mockDto.setPlanetId(1);
        mockDto.setPlanetName("Test Planet1");
        mockDto.setSystemName("Test System1");

        when(planetMoonSystemRepository.findById(mockDto.getPlanetId())).thenReturn(Optional.of(mockEntity1));

        //when
        PlanetMoonSystem result = planetMoonSystemConversionService.convertToEntity(mockDto);

        //then
        assertAll("planetMoonSystem convertToEntity_Found assertion set:",
                () -> assertNotNull(result, "planetMoonSystem convertToEntity should not return null when " +
                        "planet is found"),
                () -> assertDoesNotThrow(() -> planetMoonSystemConversionService.convertToEntity(mockDto),
                        "Converting valid PlanetMoonSystemDto to entity should not throw an exception"),
                () -> assertEquals(1, result.getPlanetId(), "PlanetMoonSystem convertToEntity " +
                        "produced an entity with the incorrect Id"),
                () -> assertEquals("Test Planet1", result.getPlanetName(), "PlanetMoonSystem " +
                        "convertToEntity produced an entity with the incorrect planet name"),
                () -> assertSame(mockEntity1, result, "planetMoonSystem after conversion does not match " +
                        "target entity"));
    }

    @Test
    void testConvertToEntity_NotFound() {
        //given
        PlanetMoonSystemDto mockDto = new PlanetMoonSystemDto();
        mockDto.setPlanetId(1);
        mockDto.setPlanetName("Test Planet1");
        mockDto.setSystemName("Test System1");

        //when entity does not exist with the same id as Dto

        //then
        assertThrows(ResourceNotFoundException.class, () -> planetMoonSystemConversionService.convertToEntity(mockDto),
                "planetMoonSystem convertToEntity should throw an exception when target entity is not found " +
                        "in repo");
    }

    @Test
    void testConvertToEntity_NameMismatch() {
        LogCaptor logCaptor = LogCaptor.forClass(PlanetMoonSystemConversionServiceImpl.class);
        //given
        //dto to convert with incorrect planet and system names
        PlanetMoonSystemDto mockDto = new PlanetMoonSystemDto();
        mockDto.setPlanetId(1);
        mockDto.setPlanetName("Wrong PlanetName");
        mockDto.setSystemName("Wrong SystemName");

        //mock parent system for entity
        PlanetarySystem mockSystemEntity = new PlanetarySystem();
        mockSystemEntity.setSystemId(1);
        mockSystemEntity.setSystemName("Test System1");

        //entity with matching id but different planet name
        PlanetMoonSystem mockEntity1 = new PlanetMoonSystem();
        mockEntity1.setPlanetId(1);
        mockEntity1.setPlanetName("Test Planet1");
        mockEntity1.setPlanetarySystem(mockSystemEntity);

        //set of mock planet entity to add to parent system's set
        Set<PlanetMoonSystem> mockPlanets = Set.of(mockEntity1);
        mockSystemEntity.setPlanetMoonSystems(mockPlanets);

        when(planetMoonSystemRepository.findById(mockDto.getPlanetId())).thenReturn(Optional.of(mockEntity1));

        PlanetMoonSystem result = planetMoonSystemConversionService.convertToEntity(mockDto);

        assertAll("PlanetMoonSystem convertToEntity name mismatch assertion set: ",
                () -> assertEquals(2, logCaptor.getErrorLogs().size(), "planetMoonSystem " +
                        "convertToEntity expected 2 error logs, 1 for incorrect planet name and 1 for " +
                        "incorrect system name"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Received wrong planet name while converting PlanetMoonSystem " +
                                "to Entity"))),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Received wrong system name while converting PlanetMoonSystem " +
                                "to Entity"))),
                () -> assertSame(mockEntity1, result, "convertToEntity is supposed to return the entity" +
                        " with the matching id regardless of whether or not the names match"));

    }
}