package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.PvpStatusRepository;
import com.sc_fleetfinder.fleets.entities.PvpStatus;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PvpStatusDto;
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
class PvpStatusConversionServiceImplTest {

    @InjectMocks
    PvpStatusConversionServiceImpl pvpStatusConversionService;

    @Mock
    PvpStatusRepository pvpStatusRepository;

    @Test
    void testConvertToDto_Success() {
        LogCaptor logCaptor = LogCaptor.forClass(PvpStatusConversionServiceImpl.class);
        //given a mock entity
        PvpStatus pvpStatus1 = new PvpStatus();
            pvpStatus1.setPvpStatusId(1);
            pvpStatus1.setPvpStatus("Status1");
        PvpStatus pvpStatus2 = new PvpStatus();
            pvpStatus2.setPvpStatusId(2);
            pvpStatus2.setPvpStatus("Status2");
        //when converting entity to dto
        List<PvpStatusDto> result = List.of(pvpStatusConversionService.convertToDto(pvpStatus1),
                pvpStatusConversionService.convertToDto(pvpStatus2));

        //then
        assertAll("PvpStatus convertToDto assertion set: ",
                () -> assertNotNull(result, "convert pvp status entity to Dto should not " +
                        "return null when given a valid Dto."),
                () -> assertDoesNotThrow(() -> pvpStatusConversionService.convertToDto(pvpStatus1),
                        "PvpStatus convertToDto should NOT throw an exception when given a " +
                                "valid entity."),
                () -> assertDoesNotThrow(() -> pvpStatusConversionService.convertToDto(pvpStatus2),
                        "PvpStatus convertToDto should NOT throw an exception when given  a " +
                                "valid entity."),
                () -> assertEquals(2, result.size(), "PvpStatus convertToDto test " +
                        "expected to produce 2 elements."),
                () -> assertEquals(1, result.getFirst().getPvpStatusId(),
                        "PvpStatus convertToDto produced a Dto with the incorrect Id"),
                () -> assertEquals("Status1", result.getFirst().getPvpStatus(),
                        "PvpStatus convertToDto produced a Dto with the incorrect status " +
                                "name"),
                () -> assertEquals(2, result.get(1).getPvpStatusId(),
                        "PvpStatus convertToDto produced a Dto with the incorrect Id"),
                () -> assertEquals("Status2", result.get(1).getPvpStatus(),
                        "PvpStatus convertToDto produced a Dto with the incorrect status " +
                                "name"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "successful " +
                        "PvpStatus conversion to Dto should NOT produce any error logs"));
    }

    @Test
    void testConvertToDto_Null_ID_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(PvpStatusConversionServiceImpl.class);
        //given a pvp status entity with a null Id
        PvpStatus pvpStatus1 = new PvpStatus();
            pvpStatus1.setPvpStatusId(null);
            pvpStatus1.setPvpStatus("Status1");

        //when
        //then
        assertAll("PvpStatus convertToDto_Null_ID assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        pvpStatusConversionService.convertToDto(pvpStatus1)),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or zero pvp status Id when " +
                                "converting from entity to Dto"))));
    }

    @Test
    void testConvertToDto_ID_Zero_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(PvpStatusConversionServiceImpl.class);
        PvpStatus pvpStatus1 = new PvpStatus();
            pvpStatus1.setPvpStatusId(0);
            pvpStatus1.setPvpStatus("Status1");

        //when
        //then
        assertAll("PvpStatus convertToDto_ID_Zero assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        pvpStatusConversionService.convertToDto(pvpStatus1)),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or zero pvp status Id when " +
                                "converting from entity to Dto"))));
    }

    @Test
    void  testConvertToDto_Null_Status_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(PvpStatusConversionServiceImpl.class);
        //given a pvp status entity with a null status name
        PvpStatus pvpStatus1 = new PvpStatus();
            pvpStatus1.setPvpStatusId(1);
            pvpStatus1.setPvpStatus(null);

        //when
        //then
        assertAll("PvpStatus convertToDto_Null_Status assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        pvpStatusConversionService.convertToDto(pvpStatus1)),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or empty pvp status name " +
                                "when converting from entity to Dto"))));
    }

    @Test
    void testConvertToDto_Empty_Status_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(PvpStatusConversionServiceImpl.class);
        //given a pvp status entity with an empty status name
        PvpStatus pvpStatus1 = new PvpStatus();
            pvpStatus1.setPvpStatusId(1);
            pvpStatus1.setPvpStatus("");

        //when
        //then
        assertAll("PvpStatus convertToDto_Empty_Status assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        pvpStatusConversionService.convertToDto(pvpStatus1)),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or empty pvp status name " +
                                "when converting from entity to Dto"))));
    }

    @Test
    void testConvertToEntity_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(PvpStatusConversionServiceImpl.class);
        //given a dto that matches an existing entity
        PvpStatusDto mockDto1 = new PvpStatusDto();
            mockDto1.setPvpStatusId(1);
            mockDto1.setPvpStatus("Status1");
        PvpStatus mockEntity1 = new PvpStatus();
            mockEntity1.setPvpStatusId(1);
            mockEntity1.setPvpStatus("Status1");

        when(pvpStatusRepository.findById(mockDto1.getPvpStatusId())).thenReturn(Optional.of(mockEntity1));

        //when
        PvpStatus result = pvpStatusConversionService.convertToEntity(mockDto1);

        //then
        assertAll("PvpStatus convertToEntity_Found assertion set: ",
                () -> assertNotNull(result, "PvpStatus convertToEntity expected to find an " +
                        "entity matching the Dto and should not return null"),
                () -> assertDoesNotThrow(() -> pvpStatusConversionService.convertToEntity(mockDto1),
                        "PvpStatus convertToEntity should not throw an exception when input " +
                                "dto is valid and found"),
                () -> assertEquals(1, result.getPvpStatusId(), "PvpStatus " +
                        "convertToEntity was expected to match the target entity but the Id's do not" +
                        " match."),
                () -> assertEquals("Status1", result.getPvpStatus(), "PvpStatus " +
                        "convertToEntity was expected to match the target entity but the name's do " +
                        "not match."),
                () -> assertSame(mockEntity1, result, "PvpStatus convertToEntity was expected " +
                        "to match the target entity but they are not the same."),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "successful " +
                        "PvpStatus convertToEntity should not produce any error logs."));

    }

    @Test
    void testConvertToEntity_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(PvpStatusConversionServiceImpl.class);
        //given a Dto with an id that does not exist in the repo
        PvpStatusDto mockDto1 = new PvpStatusDto();
            mockDto1.setPvpStatusId(1);
            mockDto1.setPvpStatus("Status1");

        //when
        //then
        assertAll("PvpStatus convertToEntity_NotFound assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        pvpStatusConversionService.convertToEntity(mockDto1)),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered pvp status with unmatched Id: 1, " +
                                "when converting from dto to entity"))));
    }

    @Test
    void testConvertToEntity_NameMismatch()  {
        LogCaptor logCaptor = LogCaptor.forClass(PvpStatusConversionServiceImpl.class);
        //given a pvp status dto with a name that does not match the name of the entity with the same id
        PvpStatusDto mockDto1 = new PvpStatusDto();
            mockDto1.setPvpStatusId(1);
            mockDto1.setPvpStatus("Wrong name");

        PvpStatus mockEntity1 = new PvpStatus();
            mockEntity1.setPvpStatusId(1);
            mockEntity1.setPvpStatus("Status1");

        when(pvpStatusRepository.findById(mockDto1.getPvpStatusId())).thenReturn(Optional.of(mockEntity1));

        //when
        //then
        assertAll("PvpStatus convertToEntity_NameMismatch assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        pvpStatusConversionService.convertToEntity(mockDto1)),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered pvp status with status name and Id mismatch when converting from Dto to entity. " +
                                "Dto Id: 1, and pvp status: Wrong name, do not match an entity"))));
    }
}