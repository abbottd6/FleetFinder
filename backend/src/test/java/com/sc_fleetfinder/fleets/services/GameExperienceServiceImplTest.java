package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class GameExperienceServiceImplTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private GameExperienceServiceImpl gameExperienceService;

    @Test
    void testGetAllExperiences() {
        GameExperience mockEntity = new GameExperience();
        List<GameExperience> mockEntities = List.of(mockEntity);
        when(experienceRepository.findAll()).thenReturn(mockEntities);

        List<GameExperienceDto> result = gameExperienceService.getAllExperiences();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(experienceRepository, times(1)).findAll();
    }

    @Test
    void testGetAllExperiencesNotFound() {
        when(experienceRepository.findAll()).thenReturn(Collections.emptyList());

        List<GameExperienceDto> result = gameExperienceService.getAllExperiences();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(experienceRepository, times(1)).findAll();
    }

    @Test
    void testGetExperienceById_Found() {
        GameExperience mockEntity = new GameExperience();
        when(experienceRepository.findById(1)).thenReturn(Optional.of(mockEntity));

        GameExperienceDto result = gameExperienceService.getExperienceById(1);

        assertNotNull(result);
        verify(experienceRepository, times(1)).findById(1);
    }

    @Test
    void testGetExperienceById_NotFound() {
        when(experienceRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gameExperienceService.getExperienceById(1));
    }

    @Test
    void testConvertToDto() {
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(1);
        mockEntity.setExperienceType("Experience1");
        GameExperience mockEntity2 = new GameExperience();
        mockEntity2.setExperienceId(2);
        mockEntity2.setExperienceType("Experience2");

        List<GameExperience> mockEntities = List.of(mockEntity, mockEntity2);
        when(experienceRepository.findAll()).thenReturn(mockEntities);

        List<GameExperienceDto> result = gameExperienceService.getAllExperiences();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.getFirst().getExperienceId());
        assertEquals("Experience1", result.getFirst().getExperienceType());
        assertEquals(2, result.get(1).getExperienceId());
        assertEquals("Experience2", result.get(1).getExperienceType());
        verify(experienceRepository, times(1)).findAll();
    }

    @Test
    void testConvertToEntity() {
        GameExperienceDto mockDto = new GameExperienceDto();
        mockDto.setExperienceId(1);
        mockDto.setExperienceType("Experience1");

        GameExperience mockEntity = gameExperienceService.convertToEntity(mockDto);

        assertNotNull(mockEntity);
        assertEquals(1, mockEntity.getExperienceId());
        assertEquals("Experience1", mockEntity.getExperienceType());
    }
}
