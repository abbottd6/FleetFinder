package com.sc_fleetfinder.fleets.services;


import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameEnvironmentServiceImplTest {

    @Mock
    private EnvironmentRepository environmentRepository;

    @InjectMocks
    private GameEnvironmentServiceImpl gameEnvironmentService;

    @Test
    void testGetAllEnvironments() {
        GameEnvironment mockEntity = new GameEnvironment();
        List<GameEnvironment> mockEntities = List.of(mockEntity);
        when(environmentRepository.findAll()).thenReturn(mockEntities);

        List<GameEnvironmentDto> result = gameEnvironmentService.getAllEnvironments();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(environmentRepository, times(1)).findAll();
    }

    @Test
    void testGetAllEnvironmentsNotFound() {
        when (environmentRepository.findAll()).thenReturn(Collections.emptyList());

        List<GameEnvironmentDto> result = gameEnvironmentService.getAllEnvironments();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(environmentRepository, times(1)).findAll();
    }

    @Test
    void testGetEnvironmentById_Found() {
        GameEnvironment mockEntity = new GameEnvironment();
        when(environmentRepository.findById(1)).thenReturn(Optional.of(mockEntity));

        GameEnvironmentDto result = gameEnvironmentService.getEnvironmentById(1);

        assertNotNull(result);
        verify(environmentRepository, times(1)).findById(1);
    }

    @Test
    void testGetEnvironmentById_NotFound() {
        when(environmentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gameEnvironmentService.getEnvironmentById(1));
    }

    @Test
    void testConvertToDto() {
        GameEnvironment mockEntity = new GameEnvironment();
        mockEntity.setEnvironmentId(1);
        mockEntity.setEnvironmentType("Environment1");
        GameEnvironment mockEntity2 = new GameEnvironment();
        mockEntity2.setEnvironmentId(2);
        mockEntity2.setEnvironmentType("Environment2");

        List<GameEnvironment> mockEntities = List.of(mockEntity, mockEntity2);
        when(environmentRepository.findAll()).thenReturn(mockEntities);

        List<GameEnvironmentDto> result = gameEnvironmentService.getAllEnvironments();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.getFirst().getEnvironmentId());
        assertEquals("Environment1", result.getFirst().getEnvironmentType());
        assertEquals(2, result.get(1).getEnvironmentId());
        assertEquals("Environment2", result.get(1).getEnvironmentType());
        verify(environmentRepository, times(1)).findAll();
    }

    @Test
    void testConvertToEntity() {
        GameEnvironmentDto mockDto = new GameEnvironmentDto();
        mockDto.setEnvironmentId(1);
        mockDto.setEnvironmentType("Environment1");

        GameEnvironment mockEntity = gameEnvironmentService.convertToEntity(mockDto);

        assertNotNull(mockEntity);
        assertEquals(1, mockEntity.getEnvironmentId());
        assertEquals("Environment1", mockEntity.getEnvironmentType());
    }
}
