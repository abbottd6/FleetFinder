package com.sc_fleetfinder.fleets.unit_tests.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.GameEnvironmentConversionServiceImpl;
import com.sc_fleetfinder.fleets.services.conversion_services.GameExperienceConversionServiceImpl;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameExperienceConversionServiceImplTest {

    @InjectMocks
    private GameExperienceConversionServiceImpl gameExperienceConversionService;

    @Mock
    private ExperienceRepository experienceRepository;

    @Test
    void testConvertToDto_Success() {
        LogCaptor logCaptor = LogCaptor.forClass(GameEnvironmentConversionServiceImpl.class);
        //given
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(1);
        mockEntity.setExperienceType("Experience1");
        GameExperience mockEntity2 = new GameExperience();
        mockEntity2.setExperienceId(2);
        mockEntity2.setExperienceType("Experience2");

        //when
        List<GameExperienceDto> result = List.of(gameExperienceConversionService.convertToDto(mockEntity),
                gameExperienceConversionService.convertToDto(mockEntity2));

        //then
        assertAll("convert experience entity to DTO assertion set:",
                () -> assertNotNull(result, "convert experience entity should not return null DTO"),
                () -> assertDoesNotThrow(() -> gameExperienceConversionService.convertToDto(mockEntity),
                        "convert experience entity to DTO should NOT throw an exception when fields are valid"),
                () -> assertDoesNotThrow(() -> gameExperienceConversionService.convertToDto(mockEntity2),
                        "convert experience entity to DTO should NOT throw an exception when fields are valid"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "Successful Experience " +
                        "convertToDto should not produce any error logs."),
                () -> assertEquals(2, result.size(), "test experience convertToDto should produce 2 " +
                        "elements"),
                () -> assertEquals(1, result.getFirst().getExperienceId(), "convert experience" +
                        " entity to DTO Id's do not match"),
                () -> assertEquals("Experience1", result.getFirst().getExperienceType(), "convert" +
                        "experience entity to DTO experienceTypes do not match"),
                () -> assertEquals(2, result.get(1).getExperienceId(), "convert experience" +
                        " entity to DTO Id's do not match"),
                () -> assertEquals("Experience2", result.get(1).getExperienceType(), "convert" +
                        "experience entity to DTO experienceTypes do not match"));
    }

    @Test
    void testConvertToDto_FailIdNull() {
        LogCaptor logCaptor = LogCaptor.forClass(GameExperienceConversionServiceImpl.class);
        //given: entity with a null id value (invalid id)
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(null);
        mockEntity.setExperienceType("Experience1");

        //when
        //then
        assertAll("Experience convertToDto_FailIdNull assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () ->
                                gameExperienceConversionService.convertToDto(mockEntity),
                        "convert experience entity to dto should throw exception when id is null"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Experience convertToDto encountered an id that is null " +
                                "or 0."))));
    }

    @Test
    void testConvertToDto_FailIdZero() {
        LogCaptor logCaptor = LogCaptor.forClass(GameExperienceConversionServiceImpl.class);
        //given: an entity with id of 0 (invalid id)
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(0);
        mockEntity.setExperienceType("Experience1");

        //when
        //then
        assertAll("Experience convertToDto_FailIdZero assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () -> gameExperienceConversionService.convertToDto(mockEntity),
                "convert experience entity to dto should throw exception when id is 0"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Experience convertToDto encountered an id that is null" +
                                " or 0."))));
    }

    //type/NAME is null
    @Test
    void testConvertToDto_FailTypeIsNull() {
        LogCaptor logCaptor = LogCaptor.forClass(GameExperienceConversionServiceImpl.class);
        //given: entity with a null type value
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(1);
        mockEntity.setExperienceType(null);

        //when
        //then
        assertAll("Experience convertToDto_FailTypeIsNull assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () -> gameExperienceConversionService.convertToDto(mockEntity),
                "convert experience entity to dto should throw an exception when type value is null"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Experience convertToDto encountered an type that is null " +
                                "or empty."))));
    }

    //type/NAME is empty
    @Test
    void testConvertToDto_FailTypeIsEmpty() {
        LogCaptor logCaptor = LogCaptor.forClass(GameExperienceConversionServiceImpl.class);
        //given: entity with an empty string for type
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(1);
        mockEntity.setExperienceType("");

        //when
        //then
        assertAll("Experience convertToDto_FailTypeIsEmpty assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () -> gameExperienceConversionService.convertToDto(mockEntity),
                "convert experience entity to dto should throw an exception when type " +
                        "value is an empty string"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Experience convertToDto encountered an type that is null " +
                                "or empty."))));
    }

    @Test
    void testConvertToEntity_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(GameExperienceConversionServiceImpl.class);
        //given: a dto that matches an entity
        GameExperienceDto mockDto = new GameExperienceDto();
        mockDto.setExperienceId(1);
        mockDto.setExperienceType("Experience1");
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(1);
        mockEntity.setExperienceType("Experience1");

        when(experienceRepository.findById(mockDto.getExperienceId())).thenReturn(Optional.of(mockEntity));

        //when
        GameExperience result = gameExperienceConversionService.convertToEntity(mockDto);

        //then
        assertAll("gameExperience convertToEntity assertion set:",
                () -> assertNotNull(result, "gameExperience convertToEntity should not return null"),
                () -> assertDoesNotThrow(() -> gameExperienceConversionService.convertToEntity(mockDto),
                        "converting valid dto to entity should not throw an exception"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "successful experience " +
                        "convertToEntity should not produce any error logs."),
                () -> assertEquals(1, result.getExperienceId(), "gameExperience convertToEntity" +
                        " experienceIds do not match"),
                () -> assertEquals("Experience1", result.getExperienceType(), "gameExperience " +
                        "convertToEntity experienceTypes do not match"),
                () -> assertSame(mockEntity, result, "convert experience dto to entity does not match " +
                        "target entity"));
    }

    @Test
    void testConvertToEntity_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(GameExperienceConversionServiceImpl.class);
        //given
        GameExperienceDto mockDto = new GameExperienceDto();
        mockDto.setExperienceId(1);
        mockDto.setExperienceType("Not Experience1");

        //when backend entity does not exist with dto ID

        //then
        assertAll("Experience convertToEntity_NotFound assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> gameExperienceConversionService.convertToEntity(mockDto),
                "convert experience Dto to entity where dto id is not found in repo should throw " +
                        "an exception"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("GameExperience convertToEntity could not find Experience " +
                                "with Id: 1"))));
    }

    @Test
    void testConvertToEntity_NameMismatch() {
        LogCaptor logCaptor = LogCaptor.forClass(GameExperienceConversionServiceImpl.class);
        //given: dto with a name that does not match the name of the entity with the same id
        GameExperienceDto mockDto = new GameExperienceDto();
        mockDto.setExperienceId(1);
        mockDto.setExperienceType("Incorrect Experience1");

        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(1);
        mockEntity.setExperienceType("Correct Experience1");

        when(experienceRepository.findById(mockDto.getExperienceId())).thenReturn(Optional.of(mockEntity));

        //when
        //then
        assertAll("Experience convertToEntity_NameMismatch assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        gameExperienceConversionService.convertToEntity(mockDto), "Experience " +
                        "convertToEntity should throw an exception when there is an id/name mismatch."),
                () ->  assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("GameExperience convertToEntity encountered an id/name " +
                                "mismatch for Id: 1, and experience: Incorrect Experience1"))));

    }
}