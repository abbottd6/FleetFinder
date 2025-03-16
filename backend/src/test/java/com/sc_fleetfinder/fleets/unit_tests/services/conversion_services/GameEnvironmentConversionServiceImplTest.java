package com.sc_fleetfinder.fleets.unit_tests.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.GameEnvironmentConversionServiceImpl;
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
class GameEnvironmentConversionServiceImplTest {

    @InjectMocks
    private GameEnvironmentConversionServiceImpl gameEnvironmentConversionService;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Test
    void testConvertToDto_Success() {
        LogCaptor logCaptor = LogCaptor.forClass(GameEnvironmentConversionServiceImpl.class);
        //given
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(1);
        mockEntity.setEnvironmentType("Environment1");
        GameEnvironment mockEntity2 = new GameEnvironment();
        mockEntity2.setEnvironmentId(2);
        mockEntity2.setEnvironmentType("Environment2");

        //when
        List<GameEnvironmentDto> result = List.of(gameEnvironmentConversionService.convertToDto(mockEntity),
                gameEnvironmentConversionService.convertToDto(mockEntity2));

        //then
        assertAll("convert environment entity to DTO assertion set:",
                () -> assertNotNull(result, "convert environment entity to DTO should not return null"),
                () -> assertDoesNotThrow(() -> gameEnvironmentConversionService.convertToDto(mockEntity),
                        "convert environment entity to DTO should NOT throw an exception when fields are valid"),
                () -> assertDoesNotThrow(() -> gameEnvironmentConversionService.convertToDto(mockEntity2),
                        "convert environment entity to DTO should NOT throw an exception when fields are valid"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "successful environment " +
                        "convertToDto should not produce any error logs."),
                () -> assertEquals(2, result.size(), "environment convertToDto should produce" +
                        "2 elements"),
                () -> assertEquals(1, result.getFirst().getEnvironmentId(), "convert environment" +
                        "entity to DTO Id's do not match"),
                () -> assertEquals("Environment1", result.getFirst().getEnvironmentType(), "convert" +
                        " environment to DTO environment types do not match"),
                () -> assertEquals(2, result.get(1).getEnvironmentId(),"convert environment" +
                        "entity to DTO Id's do not match"),
                () -> assertEquals("Environment2", result.get(1).getEnvironmentType(), "convert" +
                        " environment to DTO environment types do not match"));
    }

    @Test
    void testConvertToDto_FailIdNull() {
        LogCaptor logCaptor = LogCaptor.forClass(GameEnvironmentConversionServiceImpl.class);
        //given
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(null);
        mockEntity.setEnvironmentType("Environment1");

        //when
        //then
        assertAll("Environment convertToDto_FailIdNull assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () -> gameEnvironmentConversionService.convertToDto(mockEntity),
                "convert environment entity to dto should throw exception when Id is null"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Environment convertToDto encountered a null or zero Id value."))));
    }

    @Test
    void testConvertToDto_FailIdZero() {
        LogCaptor logCaptor = LogCaptor.forClass(GameEnvironmentConversionServiceImpl.class);
        //given: an entity with id of 0 (invalid id)
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(0);
        mockEntity.setEnvironmentType("Environment1");

        //when
        //then
        assertAll("Environment convertToDto_FailIdZero assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () -> gameEnvironmentConversionService.convertToDto(mockEntity),
                "convert environment entity to dto should throw exception when Id is 0"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Environment convertToDto encountered a null or zero Id value."))));
    }

    @Test
    void testConvertToDto_FailTypeIsNull() {
        LogCaptor logCaptor = LogCaptor.forClass(GameEnvironmentConversionServiceImpl.class);
        //given
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(1);
        mockEntity.setEnvironmentType(null);

        //when
        //then
        assertAll("Environment convertToDto_FailTypeIsNull assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () -> gameEnvironmentConversionService.convertToDto(mockEntity),
                "convert environment entity to dto should throw exception when type is null"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Environment convertToDto environment type is null or empty."))));
    }

    @Test
    void testConvertToDto_FailTypeIsEmptyString() {
        LogCaptor logCaptor = LogCaptor.forClass(GameEnvironmentConversionServiceImpl.class);
        //given: entity with a null type value
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(1);
        mockEntity.setEnvironmentType("");

        //when
        //then
        assertAll("Environment convertToDto_FailTypeIsEmptyString assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () -> gameEnvironmentConversionService.convertToDto(mockEntity),
                "convert environment entity to dto should throw an exception when type " +
                        "value is an empty string"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Environment convertToDto environment type is null or empty."))));
    }

    @Test
    void testConvertToEntity_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(GameEnvironmentConversionServiceImpl.class);
        //given
        GameEnvironmentDto mockDto = new GameEnvironmentDto();
        mockDto.setEnvironmentId(1);
        mockDto.setEnvironmentType("Environment1");

        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(1);
        mockEntity.setEnvironmentType("Environment1");

        when(environmentRepository.findById(mockDto.getEnvironmentId())).thenReturn(Optional.of(mockEntity));

        //when
        GameEnvironment result = gameEnvironmentConversionService.convertToEntity(mockDto);

        //then
        assertAll("gameEnvironment convertToEntity assertion set:",
                () -> assertNotNull(result, "gameEnvironment convertToEntity should not return null"),
                () -> assertDoesNotThrow(() -> gameEnvironmentConversionService.convertToEntity(mockDto),
                        "Valid dto converting to entity should not throw an exception"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "successful Environment " +
                        "convertToEntity should not produce any error logs."),
                () -> assertEquals(1, result.getEnvironmentId(), "gameEnvironment convertToEntity" +
                        " environmentIds do not match"),
                () -> assertEquals("Environment1", result.getEnvironmentType(), "gameEnvironment " +
                        "convertToEntity environmentTypes do not match"),
                () -> assertSame(mockEntity, result, "converted environment dto to entity does not match " +
                        "target entity"));
    }

    @Test
    void testConvertToEntity_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(GameEnvironmentConversionServiceImpl.class);
        //given
        GameEnvironmentDto mockEnvironmentDto = new GameEnvironmentDto();
        mockEnvironmentDto.setEnvironmentId(1);
        mockEnvironmentDto.setEnvironmentType("Not Environment1");

        //when backend entity does not exist with dto ID

        //then
        assertAll("Environment convertToEntity_NotFound assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        gameEnvironmentConversionService.convertToEntity(mockEnvironmentDto),
                "convert environment dto to entity where dto id is not found in repo should throw " +
                        "an exception"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Environment convertToEntity could not find env with Id: 1"))));
    }

    @Test
    void testConvertToEntity_NameMismatch() {
        LogCaptor logCaptor = LogCaptor.forClass(GameEnvironmentConversionServiceImpl.class);
        //given dto and entity
        GameEnvironmentDto mockDto = new GameEnvironmentDto();
        mockDto.setEnvironmentId(1);
        mockDto.setEnvironmentType("Incorrect Environment1");

        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(1);
        mockEntity.setEnvironmentType("Correct Environment1");

        when(environmentRepository.findById(mockDto.getEnvironmentId())).thenReturn(Optional.of(mockEntity));


        //when dto and entity type fields do not match

        //then
        assertAll("EnvironmentConvertToEntity_NameMismatch assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        gameEnvironmentConversionService.convertToEntity(mockDto), "environment " +
                        "convertToEntity should throw an exception when there is an id/name mismatch"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Environment convertToEntity encountered a id/name mismatch " +
                                "with Id: 1, and environment type: Incorrect Environment1"))));
    }
}