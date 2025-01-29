package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.Collections;
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
        GameEnvironment mockEntity2 = new GameEnvironment();
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
        //given
        when (environmentRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<GameEnvironmentDto> result = gameEnvironmentService.getAllEnvironments();

        //then
        assertAll("get all environments = empty, assertion set:",
                () -> assertNotNull(result, "getAllEnvironments should be empty, not null"),
                () -> assertTrue(result.isEmpty(), "getAllEnvironments returned " + result + " when " +
                        "it should have returned empty"),
                () -> verify(environmentRepository, times(1)).findAll());
    }

    @Test
    void testGetEnvironmentById_Found() {
        //given
        GameEnvironment mockEntity = new GameEnvironment();
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
        //given
        when(environmentRepository.findById(1)).thenReturn(Optional.empty());

        //when environmentRepository does not contain environment with given Id

        //then
        assertAll("get environmentById=not found assertion set:",
                () -> assertThrows(ResourceNotFoundException.class, () -> gameEnvironmentService.getEnvironmentById(1),
                "getEnvironmentById with id not found should throw exception"));
    }

    @Test
    void testConvertToDto() {
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
                () -> verify(environmentRepository, times(1)).findAll());
    }

    @Test
    void testConvertToEntity() {
        //given
        GameEnvironmentDto mockDto = new GameEnvironmentDto();
        mockDto.setEnvironmentId(1);
        mockDto.setEnvironmentType("Environment1");

        //when
        GameEnvironment mockEntity = gameEnvironmentService.convertToEntity(mockDto);

        //then
        assertAll("gameEnvironment convertToEntity assertions set:",
                () -> assertNotNull(mockEntity, "gameEnvironment convertToEntity should not return null"),
                () -> assertEquals(1, mockEntity.getEnvironmentId(), "gameEnvironment convertToEntity" +
                        " environmentIds do not match"),
                () -> assertEquals("Environment1", mockEntity.getEnvironmentType(), "gameEnvironment " +
                        "convertToEntity environmentTypes do not match"));
    }
}
