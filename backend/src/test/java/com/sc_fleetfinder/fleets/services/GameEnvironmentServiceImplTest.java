package com.sc_fleetfinder.fleets.services;


import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
