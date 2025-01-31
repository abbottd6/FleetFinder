package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.GameEnvironmentServiceImpl;
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
class EnvironmentCachingServiceImplTest {

    @Mock
    private EnvironmentRepository environmentRepository;

    @Mock
    private GameEnvironmentServiceImpl gameEnvironmentService;

    @InjectMocks
    private EnvironmentCachingServiceImpl environmentCachingService;

    @Test
    void testCacheAllEnvironments_Found() {
        //given
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(1);
        mockEntity.setEnvironmentType("Test Env1");
        GameEnvironment mockEntity2 = new GameEnvironment();
        mockEntity2.setEnvironmentId(2);
        mockEntity2.setEnvironmentType("Test Env2");
        List<GameEnvironment> mockEntities = List.of(mockEntity, mockEntity2);
        when(environmentRepository.findAll()).thenReturn(mockEntities);
        GameEnvironmentDto mockDto = new GameEnvironmentDto();
        mockDto.setEnvironmentId(1);
        mockDto.setEnvironmentType("Test Env1");
        GameEnvironmentDto mockDto2 = new GameEnvironmentDto();
        mockDto2.setEnvironmentId(2);
        mockDto2.setEnvironmentType("Test Env2");
        when(gameEnvironmentService.convertToDto(mockEntity)).thenReturn(mockDto);
        when(gameEnvironmentService.convertToDto(mockEntity2)).thenReturn(mockDto2);

        //when
        List<GameEnvironmentDto> result = environmentCachingService.cacheAllEnvironments();

        //then
        assertAll("getAllEnvironments mock entities assertion set:",
                () -> assertNotNull(result, "getAllEnvironments should not return null"),
                () -> assertEquals(2, result.size(), "Get all environments produced unexpected " +
                        "number of results"),
                () -> verify(environmentRepository, times(1)).findAll());
    }

    @Test
    void testCacheAllEnvironments_NotFound() {
        //given environments repo is empty

        //when
        //then
        assertAll("getAllEnvironments = empty assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> environmentCachingService.cacheAllEnvironments()),
                () -> verify(environmentRepository, times(1)).findAll());
    }
}