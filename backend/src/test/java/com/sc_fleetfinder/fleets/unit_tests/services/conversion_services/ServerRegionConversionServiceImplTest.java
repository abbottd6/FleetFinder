package com.sc_fleetfinder.fleets.unit_tests.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.ServerRegionRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.entities.ServerRegion;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.ServerRegionConversionServiceImpl;
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
class ServerRegionConversionServiceImplTest {

    @InjectMocks
    private ServerRegionConversionServiceImpl serverRegionConversionService;

    @Mock
    private ServerRegionRepository serverRegionRepository;

    @Test
    void testConvertToDto_Success() {
        LogCaptor logCaptor = LogCaptor.forClass(ServerRegionConversionServiceImpl.class);
        //given: mock entities to convert
        ServerRegion mockEntity1 = new ServerRegion();
            mockEntity1.setServerId(1);
            mockEntity1.setServerName("Server1");
        ServerRegion mockEntity2 = new ServerRegion();
            mockEntity2.setServerId(2);
            mockEntity2.setServerName("Server2");

        //when
        List<ServerRegionDto> result = List.of(serverRegionConversionService.convertToDto(mockEntity1),
                serverRegionConversionService.convertToDto(mockEntity2));

        //then
        assertAll("ServerRegion convertToDto_Success assertion set: ",
                () -> assertNotNull(result, "expected successful ServerRegion convertToDto, should not " +
                        "return null"),
                () -> assertDoesNotThrow(() -> serverRegionConversionService.convertToDto(mockEntity1),
                        "ServerRegion convertToDto should not throw an exception when fields are valid."),
                () -> assertDoesNotThrow(() -> serverRegionConversionService.convertToDto(mockEntity2),
                        "ServerRegion convertToDto should not throw an exception when fields are valid."),
                () -> assertEquals(1, result.getFirst().getServerId(), "ServerRegion " +
                        "convertToDto produced a Dto with the incorrect Id."),
                () -> assertEquals("Server1", result.getFirst().getServername(), "ServerRegion " +
                        "convertToDto produced a Dto with the incorrect server name."),
                () -> assertEquals(2, result.get(1).getServerId(), "ServerRegion " +
                        "convertToDto produced a Dto with the incorrect server Id."),
                () -> assertEquals("Server2", result.get(1).getServername(), "ServerRegion " +
                        "convertToDto produced a Dto with the incorrect server name."),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "Successful ServerRegion " +
                        "convertToDto should not produce any error logs."));
    }

    @Test
    void testConvertToDto_ID_Null_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(ServerRegionConversionServiceImpl.class);
        //given: entity with null id to convert
        ServerRegion mockEntity1 = new ServerRegion();
            mockEntity1.setServerId(null);
            mockEntity1.setServerName("Server1");

        //when
        //then
        assertAll("ServerRegion convertToDto_ID_Null assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () ->
                        serverRegionConversionService.convertToDto(mockEntity1),"ServerRegion " +
                        "convertToDto should throw an exception when id is null"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or zero server Id when converting from " +
                                "entity to Dto."))));
    }

    @Test
    void testConvertToDto_ID_Zero_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(ServerRegionConversionServiceImpl.class);
        //given: server entity with id == 0
        ServerRegion mockEntity1 = new ServerRegion();
            mockEntity1.setServerId(0);
            mockEntity1.setServerName("Server1");

        //when
        //then
        assertAll("ServerRegion convertToDto_ID_Zero assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () ->
                        serverRegionConversionService.convertToDto(mockEntity1), "ServerRegion " +
                        "convertToDto should throw an exception when id == 0."),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or zero server Id when converting from " +
                                "entity to Dto."))));
    }

    @Test
    void testConvertToDto_Name_Null_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(ServerRegionConversionServiceImpl.class);
        //given: server entity with a null server name
        ServerRegion mockEntity1 = new ServerRegion();
            mockEntity1.setServerId(1);
            mockEntity1.setServerName(null);

        //when
        //then
        assertAll("ServerRegion convertToDto_Name_Null assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () ->
                        serverRegionConversionService.convertToDto(mockEntity1), "ServerRegion " +
                        "convertToDto should throw an exception when server name is null."),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or empty server Name when converting from " +
                                "entity to Dto."))));
    }

    @Test
    void testConvertToDto_Name_Empty_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(ServerRegionConversionServiceImpl.class);
        //given: server entity with an empty string for server name
        ServerRegion mockEntity1 = new ServerRegion();
            mockEntity1.setServerId(1);
            mockEntity1.setServerName("");

        //when
        //then
        assertAll("ServerRegion convertToDto_Name_Empty assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () ->
                        serverRegionConversionService.convertToDto(mockEntity1), "ServerRegion " +
                        "convertToDto should throw an exception when server name is empty string."),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered null or empty server Name when converting from " +
                                "entity to Dto."))));
    }

    @Test
    void testConvertToEntity_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(ServerRegionConversionServiceImpl.class);
        //given: server dto to convert to entity
        ServerRegionDto mockDto1 = new ServerRegionDto();
            mockDto1.setServerId(1);
            mockDto1.setServername("Server1");
        //and an existing entity that matches the dto attributes
        ServerRegion mockEntity1 = new ServerRegion();
            mockEntity1.setServerId(1);
            mockEntity1.setServerName("Server1");
        when(serverRegionRepository.findById(mockDto1.getServerId())).thenReturn(Optional.of(mockEntity1));

        //when
        ServerRegion result = serverRegionConversionService.convertToEntity(mockDto1);

        //then
        assertAll("ServerRegion convertToEntity_Found assertion set: ",
                () -> assertNotNull(result, "expected successful serverRegion convertToEntity, " +
                        "should not return null"),
                () -> assertDoesNotThrow(() -> serverRegionConversionService.convertToEntity(mockDto1),
                        "expected successful serverRegion convertToEntity, should not throw an exception."),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "Successful ServerRegion " +
                        "convertToEntity should not produce any error logs."),
                () -> assertEquals(1, result.getServerId(), "ServerRegion convertToEntity " +
                        "produced an entity with the incorrect Id."),
                () -> assertEquals("Server1", result.getServerName(), "ServerRegion " +
                        "convertToEntity produced an entity with the incorrect server name."),
                () -> assertSame(mockEntity1, result, "ServerRegion convertToEntity does not match target " +
                        "entity."));

    }

    @Test
    void testConvertToEntity_IdNotFound_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(ServerRegionConversionServiceImpl.class);
        //given: dto that does not match a backend entity
        ServerRegionDto mockDto1 = new ServerRegionDto();
            mockDto1.setServerId(1);
            mockDto1.setServername("Server1");

        //when
        //then
        assertAll("ServerRegion convertToEntity_IdNotFound assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        serverRegionConversionService.convertToEntity(mockDto1), "ServerRegion " +
                        "convertToEntity should throw an exception when an entity with the matching Id does " +
                        "not exist"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered server with unmatched Id: 1, when converting " +
                                "from dto to entity"))));
    }

    @Test
    void testConvertToEntity_NameMismatch_Failure() {
        LogCaptor logCaptor = LogCaptor.forClass(ServerRegionConversionServiceImpl.class);
        //given: dto with a name that does not match the name of the entity with the same id
        ServerRegionDto mockDto1 = new ServerRegionDto();
            mockDto1.setServerId(1);
            mockDto1.setServername("Incorrect name");

        ServerRegion mockEntity1 = new ServerRegion();
            mockEntity1.setServerId(1);
            mockEntity1.setServerName("Server1");
        when(serverRegionRepository.findById(mockDto1.getServerId())).thenReturn(Optional.of(mockEntity1));

        //when
        //then
        assertAll("ServerRegion convertToEntity_NameMismatch assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () ->
                        serverRegionConversionService.convertToEntity(mockDto1), "ServerRegion " +
                        "convertToEntity should throw an exception when dto and target entity names do not match."),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Encountered server with status name and Id mismatch when " +
                                "converting from dto to entity. Id: 1 - server name: Incorrect name"))));
    }
}