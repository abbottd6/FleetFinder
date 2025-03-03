package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.PlayStyleRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlayStyleDto;
import com.sc_fleetfinder.fleets.entities.PlayStyle;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
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
class PlayStyleConversionServiceImplTest {

    @InjectMocks
    PlayStyleConversionServiceImpl playStyleConversionService;

    @Mock
    PlayStyleRepository playStyleRepository;

    @Test
    void testConvertToDto_Success() {
        LogCaptor logCaptor = LogCaptor.forClass(PlayStyleConversionServiceImpl.class);
        //given a mock entity
        PlayStyle mockEntity1 = new PlayStyle();
            mockEntity1.setStyleId(1);
            mockEntity1.setPlayStyle("Style1");
        PlayStyle mockEntity2 = new PlayStyle();
            mockEntity2.setStyleId(2);
            mockEntity2.setPlayStyle("Style2");
        //when converting entity to dto
        List<PlayStyleDto> result = List.of(playStyleConversionService.convertToDto(mockEntity1),
                playStyleConversionService.convertToDto(mockEntity2));

        //then
        assertAll("PlayStyle convertToDto assertion set: ",
                () -> assertNotNull(result, "convert play style entity to DTO should not return null"),
                () -> assertDoesNotThrow(() -> playStyleConversionService.convertToDto(mockEntity1),
                        "convert play style entity to DTO should NOT throw an exception when fields are valid"),
                () -> assertDoesNotThrow(() -> playStyleConversionService.convertToDto(mockEntity2),
                        "convert play style entity to  DTO should NOT throw an exception when fields are valid"),
                () -> assertEquals(2, result.size(), "test convert play styles to Dto's should " +
                        "produce 2 elements"),
                () -> assertEquals(1, result.getFirst().getStyleId(), "play style convert to DTO" +
                        "produced a Dto with the incorrect Id"),
                () -> assertEquals("Style1", result.getFirst().getPlayStyle(), "play style convert " +
                        "to Dto produced a Dto with an incorrect style name"),
                () -> assertEquals(2, result.get(1).getStyleId(), "play style convert to Dto " +
                        "produced a Dto with the incorrect Id"),
                () -> assertEquals("Style2", result.get(1).getPlayStyle(), "play style convert to " +
                        "Dto produced a Dto with the incorrect style name"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "successful play style " +
                        "conversion to entity should NOT produce any error logs"));
    }

    @Test
    void testConvertToDto_Null_ID_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(PlayStyleConversionServiceImpl.class);
        //given a play style entity with a null id
        PlayStyle mockEntity1 = new PlayStyle();
            mockEntity1.setStyleId(null);
            mockEntity1.setPlayStyle("Style1");

        //When
        //then
        assertAll("play style convertToDto_Null_Id_Failure assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        playStyleConversionService.convertToDto(mockEntity1)),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or zero play style Id when converting from " +
                                "entity to Dto"))));
    }

    @Test
    void testConvertToDto_ID_Zero_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(PlayStyleConversionServiceImpl.class);
        //given a play style entity with an id of 0
        PlayStyle mockEntity1 = new PlayStyle();
            mockEntity1.setStyleId(0);
            mockEntity1.setPlayStyle("Style1");

        //when
        //then
        assertAll("play style convertToDto_ID_Zero_Failure assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        playStyleConversionService.convertToDto(mockEntity1)),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or zero play style Id when converting from " +
                                "entity to Dto"))));
    }

    @Test
    void testConvertToDto_Null_Style_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(PlayStyleConversionServiceImpl.class);
        //given a play style entity with a null style name
        PlayStyle mockEntity1 = new PlayStyle();
            mockEntity1.setStyleId(1);
            mockEntity1.setPlayStyle(null);

        //when
        //then
        assertAll("play style convertToDto_Null_Style_Failure assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        playStyleConversionService.convertToDto(mockEntity1)),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or empty play style name when converting " +
                                "from entity to Dto"))));
    }

    @Test
    void testConvertToDto_Empty_Style_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(PlayStyleConversionServiceImpl.class);
        //given a play style entity with an empty string for style name
        PlayStyle mockEntity1 = new PlayStyle();
            mockEntity1.setStyleId(1);
            mockEntity1.setPlayStyle("");

        //when
        //then
        assertAll("play style convertToDto_Empty_Style_Failure assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        playStyleConversionService.convertToDto(mockEntity1)),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or empty play style name when converting " +
                                "from entity to Dto"))));
    }

    @Test
    void testConvertToEntity_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(PlayStyleConversionServiceImpl.class);
        //given a dto that matches an existing entity
        PlayStyleDto mockDto = new PlayStyleDto();
            mockDto.setStyleId(1);
            mockDto.setPlayStyle("Style1");
        PlayStyle mockEntity1 = new PlayStyle();
            mockEntity1.setStyleId(1);
            mockEntity1.setPlayStyle("Style1");

        when(playStyleRepository.findById(mockDto.getStyleId())).thenReturn(Optional.of(mockEntity1));

        //when
        PlayStyle result = playStyleConversionService.convertToEntity(mockDto);

        //then
        assertAll("play style convertToEntity_Found assertion set: ",
                () -> assertNotNull(result, "play style convertToEntity expected to find an entity matching " +
                        "the Dto and should not return null"),
                () -> assertDoesNotThrow(() -> playStyleConversionService.convertToEntity(mockDto),
                        "play style convertToEntity should not throw an exception when input dto is valid and" +
                                " found"),
                () -> assertEquals(1, result.getStyleId(), "play style convertToEntity produced an " +
                        "entity with the incorrect Id"),
                () -> assertEquals("Style1", result.getPlayStyle(), "play style convertToEntity " +
                        "produced an entity with the incorrect style name"),
                () -> assertSame(mockEntity1, result, "play style convertToEntity was expected to match the " +
                        "target entity but it does not"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "successful play style " +
                        "convertToEntity should not produce any error logs."));
    }

    @Test
    void testConvertToEntity_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(PlayStyleConversionServiceImpl.class);
        //given a Dto with an id that does not exist in the repo
        PlayStyleDto mockDto = new PlayStyleDto();
            mockDto.setStyleId(1);
            mockDto.setPlayStyle("Style1");

        //when
        //then
        assertAll("play style convertToEntity_NotFound assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        playStyleConversionService.convertToEntity(mockDto)),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered play style with unmatched Id: 1, " +
                                "when converting from dto to entity"))));
    }

    @Test
    void testConvertToDto_NameMismatch() {
        LogCaptor logCaptor = LogCaptor.forClass(PlayStyleConversionServiceImpl.class);
        //given a play style dto with a name that does not match the entity with the corresponding id
        PlayStyleDto mockDto = new PlayStyleDto();
            mockDto.setStyleId(1);
            mockDto.setPlayStyle("Wrong Name");
        PlayStyle mockEntity1 = new PlayStyle();
            mockEntity1.setStyleId(1);
            mockEntity1.setPlayStyle("Style1");
        when(playStyleRepository.findById(mockDto.getStyleId())).thenReturn(Optional.of(mockEntity1));

        //when
        //then
        assertAll("play style convertToEntity_NameMismatch assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        playStyleConversionService.convertToEntity(mockDto)),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered play style with style name and Id mismatch: " +
                                "Wrong Name, when converting from Dto to entity"))));
    }
}