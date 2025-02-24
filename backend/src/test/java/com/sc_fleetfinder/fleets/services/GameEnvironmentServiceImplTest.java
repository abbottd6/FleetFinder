package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.services.caching_services.EnvironmentCachingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameEnvironmentServiceImplTest {

    @Mock
    private EnvironmentCachingServiceImpl environmentCachingService;

    @InjectMocks
    private GameEnvironmentServiceImpl gameEnvironmentService;

    @Test
    void testGetAllEnvironments_Found() {
        //given
        GameEnvironmentDto mockDto = new GameEnvironmentDto();
        mockDto.setEnvironmentId(1);
        mockDto.setEnvironmentType("Test Env1");
        GameEnvironmentDto mockDto2 = new GameEnvironmentDto();
        mockDto2.setEnvironmentId(2);
        mockDto2.setEnvironmentType("Test Env2");
        List<GameEnvironmentDto> mockDtoes = List.of(mockDto, mockDto2);
        when(environmentCachingService.cacheAllEnvironments()).thenReturn(mockDtoes);

        //when
        List<GameEnvironmentDto> result = gameEnvironmentService.getAllEnvironments();

        //then
        assertAll("getAllEnvironments mock entities assertion set:",
                () -> assertNotNull(result, "getAllEnvironments should not return null"),
                () -> assertEquals(2, result.size(), "Get all environments produced unexpected " +
                        "number of results"),
                () -> verify(environmentCachingService, times(1)).cacheAllEnvironments());
    }

    @Test
    void testGetAllEnvironments_NotFound() {
        //given: environments repo is empty

        //telling the test to throw an exception when it tries to cache an empty repo
        when(environmentCachingService.cacheAllEnvironments()).thenThrow(ResourceNotFoundException.class);

        //when
        //then
        assertAll("getAllEnvironments = empty assertion set: ",
                //verifies that the exception propagates correctly to the getAll method
                () -> assertThrows(ResourceNotFoundException.class, () -> gameEnvironmentService.getAllEnvironments()),
                () -> verify(environmentCachingService, times(1)).cacheAllEnvironments());
    }

    @Test
    void testGetEnvironmentById_Found() {
        //given
        GameEnvironmentDto mockDto = new GameEnvironmentDto();
        mockDto.setEnvironmentId(1);
        mockDto.setEnvironmentType("Test Env1");
        GameEnvironmentDto mockDto2 = new GameEnvironmentDto();
        mockDto2.setEnvironmentId(2);
        mockDto2.setEnvironmentType("Test Env2");
        List<GameEnvironmentDto> mockDtoes = List.of(mockDto, mockDto2);
        when(environmentCachingService.cacheAllEnvironments()).thenReturn(mockDtoes);

        //when
        GameEnvironmentDto result = gameEnvironmentService.getEnvironmentById(1);

        //then
        assertAll("Get environment by Id=found assertion set:",
                () -> assertNotNull(result, "Found environmentId should not return a null DTO"),
                () -> assertDoesNotThrow(() -> gameEnvironmentService.getEnvironmentById(1),
                        "getEnvironmentById should not throw exception when Id is found"),
                () -> verify(environmentCachingService, times(2)).cacheAllEnvironments());
    }

    @Test
    void testGetEnvironmentById_NotFound() {
        //given: environmentRepository does not contain environment with given Id

        //when
        //then
        assertAll("getEnvironmentById = not found assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> gameEnvironmentService.getEnvironmentById(1),
                "getEnvironmentById with id not found should throw exception"),
                () -> verify(environmentCachingService, times(1)).cacheAllEnvironments());
    }
}
