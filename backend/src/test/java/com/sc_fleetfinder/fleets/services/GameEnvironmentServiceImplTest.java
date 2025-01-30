package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameEnvironmentServiceImplTest {

    @Mock
    //@AfterEach MockitoExtension @Mock resets repo
    private EnvironmentRepository environmentRepository;

    @InjectMocks
    private GameEnvironmentServiceImpl gameEnvironmentService;

    @Test
    void testGetAllEnvironments_Found() {
        //given
        GameEnvironment mockEntity = new GameEnvironment();
            mockEntity.setEnvironmentId(1);
            mockEntity.setEnvironmentType("Test Env1");
        GameEnvironment mockEntity2 = new GameEnvironment();
            mockEntity2.setEnvironmentId(2);
            mockEntity2.setEnvironmentType("Test Env2");
        List<GameEnvironment> mockEntities = List.of(mockEntity, mockEntity2);
        when(environmentRepository.findAll()).thenReturn(mockEntities);

        //when
        List<GameEnvironmentDto> result = gameEnvironmentService.getAllEnvironments();

        //then
        assertAll("getAllEnvironments mock entities assertion set:",
                () -> assertNotNull(result, "getAllEnvironments should not return null"),
                () -> assertEquals(2, result.size(), "Get all environments produced unexpected " +
                        "number of results"),
                () -> verify(environmentRepository, times(1)).findAll());
    }

    @Test
    void testGetAllEnvironments_NotFound() {
        //given environments repo is empty

        //when
        //then
        assertAll("getAllEnvironments = empty assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> gameEnvironmentService.getAllEnvironments()),
                () -> verify(environmentRepository, times(1)).findAll());
    }

    @Test
    void testGetEnvironmentById_Found() {
        //given
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(1);
        mockEntity.setEnvironmentType("Test Env1");
        when(environmentRepository.findById(1)).thenReturn(Optional.of(mockEntity));

        //when
        GameEnvironmentDto result = gameEnvironmentService.getEnvironmentById(1);

        //then
        assertAll("Get environment by Id=found assertion set:",
                () -> assertNotNull(result, "Found environmentId should not return a null DTO"),
                () -> assertDoesNotThrow(() -> gameEnvironmentService.getEnvironmentById(1),
                        "getEnvironmentById should not throw exception when Id is found"),
                () -> verify(environmentRepository, times(2)).findById(1));
    }

    @Test
    void testGetEnvironmentById_NotFound() {
        //given environmentRepository does not contain environment with given Id

        //when
        //then
        assertAll("getEnvironmentById = not found assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> gameEnvironmentService.getEnvironmentById(1),
                "getEnvironmentById with id not found should throw exception"),
                () -> verify(environmentRepository, times(1)).findById(1));
    }

    @Test
    void testConvertToDto_Success() {
        //given
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(1);
        mockEntity.setEnvironmentType("Environment1");
        GameEnvironment mockEntity2 = new GameEnvironment();
        mockEntity2.setEnvironmentId(2);
        mockEntity2.setEnvironmentType("Environment2");

        List<GameEnvironment> mockEntities = List.of(mockEntity, mockEntity2);
        when(environmentRepository.findAll()).thenReturn(mockEntities);

        //when
        List<GameEnvironmentDto> result = gameEnvironmentService.getAllEnvironments();

        //then
        assertAll("convert environment entity to DTO assertion set:",
                () -> assertNotNull(result, "convert environment entity to DTO should not return null"),
                () -> assertDoesNotThrow(() -> gameEnvironmentService.getAllEnvironments(),
                        "convert environment entity to DTO should NOT throw an exception"),
                () -> assertEquals(2, result.size(), "environment convertToDto should produce" +
                        "2 elements"),
                () -> assertEquals(1, result.getFirst().getEnvironmentId(), "convert environment" +
                        "entity to DTO Id's do not match"),
                () -> assertEquals("Environment1", result.getFirst().getEnvironmentType(), "convert" +
                        " environment to DTO environment types do not match"),
                () -> assertEquals(2, result.get(1).getEnvironmentId(),"convert environment" +
                        "entity to DTO Id's do not match"),
                () -> assertEquals("Environment2", result.get(1).getEnvironmentType(), "convert" +
                        " environment to DTO environment types do not match"),
                () -> verify(environmentRepository, times(2)).findAll());
    }

    @Test
    void testConvertToDto_FailIdNull() {
        //given
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(null);
        mockEntity.setEnvironmentType("Environment1");

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> gameEnvironmentService.convertToDto(mockEntity),
                "convert environment entity to dto should throw exception when Id is null");
    }

    @Test
    void testConvertToDto_FailIdZero() {
        //given
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(0);
        mockEntity.setEnvironmentType("Environment1");

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> gameEnvironmentService.convertToDto(mockEntity),
                "convert environment entity to dto should throw exception when Id is null");
    }

    @Test
    void testConvertToDto_FailTypeIsNull() {
        //given
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(1);
        mockEntity.setEnvironmentType(null);

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> gameEnvironmentService.convertToDto(mockEntity),
                "convert environment entity to dto should throw exception when Id is null");
    }

    @Test
    void testConvertToDto_FailTypeIsEmptyString() {
        //given
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(1);
        mockEntity.setEnvironmentType("");

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> gameEnvironmentService.convertToDto(mockEntity),
                "convert environment entity to dto should throw exception when Id is null");
    }

    @Test
    void testConvertToEntity_Found() {
        //given
        GameEnvironmentDto mockDto = new GameEnvironmentDto();
        mockDto.setEnvironmentId(1);
        mockDto.setEnvironmentType("Environment1");

        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(1);
        mockEntity.setEnvironmentType("Environment1");

        when(environmentRepository.findById(mockDto.getEnvironmentId())).thenReturn(Optional.of(mockEntity));

        //when
        GameEnvironment result = gameEnvironmentService.convertToEntity(mockDto);

        //then
        assertAll("gameEnvironment convertToEntity assertions set:",
                () -> assertNotNull(result, "gameEnvironment convertToEntity should not return null"),
                () -> assertDoesNotThrow(() -> gameEnvironmentService.convertToEntity(mockDto),
                        "Valid dto converting to entity should not throw an exception"),
                () -> assertEquals(1, result.getEnvironmentId(), "gameEnvironment convertToEntity" +
                        " environmentIds do not match"),
                () -> assertEquals("Environment1", result.getEnvironmentType(), "gameEnvironment " +
                        "convertToEntity environmentTypes do not match"),
                () -> assertSame(result, mockEntity, "convert environment entity to entity do not match"));
    }

    @Test
    void testConvertToEntity_NotFound() {
        //given
        GameEnvironmentDto mockEnvironmentDto = new GameEnvironmentDto();
        mockEnvironmentDto.setEnvironmentId(1);
        mockEnvironmentDto.setEnvironmentType("Not Environment1");

        //when backend entity does not exist with dto ID

        //then
        assertThrows(ResourceNotFoundException.class, () -> gameEnvironmentService.convertToEntity(mockEnvironmentDto),
                "convertToEntity with id not found should throw exception");
    }

    @Test
    void testConvertToEntity_NameMismatch() {
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
        assertThrows(ResourceNotFoundException.class, () -> gameEnvironmentService.convertToEntity(mockDto));
    }
}
