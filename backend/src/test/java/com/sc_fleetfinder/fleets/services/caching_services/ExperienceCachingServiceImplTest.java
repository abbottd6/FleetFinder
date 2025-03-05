package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.GameExperienceConversionServiceImpl;
import nl.altindag.log.LogCaptor;
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
class ExperienceCachingServiceImplTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private GameExperienceConversionServiceImpl experienceConversionService;

    @InjectMocks
    private ExperienceCachingServiceImpl experienceCachingService;

    @Test
    void testCacheAllExperiences_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(ExperienceCachingServiceImpl.class);
        //given
        //mock entities for find all
        GameExperience mockEntity = new GameExperience();
            mockEntity.setExperienceId(1);
            mockEntity.setExperienceType("Test Exp1");
        GameExperience mockEntity2 = new GameExperience();
            mockEntity2.setExperienceId(2);
            mockEntity2.setExperienceType("Test Exp2");
        List<GameExperience> mockEntities = List.of(mockEntity, mockEntity2);
        when(experienceRepository.findAll()).thenReturn(mockEntities);

        //mock dtoes to return from conversion in cacheALl
        GameExperienceDto mockDto1 = new GameExperienceDto();
            mockDto1.setExperienceId(1);
            mockDto1.setExperienceType("Test Exp1");
        GameExperienceDto mockDto2 = new GameExperienceDto();
            mockDto2.setExperienceId(2);
            mockDto2.setExperienceType("Test Exp2");
        when(experienceConversionService.convertToDto(mockEntity)).thenReturn(mockDto1);
        when(experienceConversionService.convertToDto(mockEntity2)).thenReturn(mockDto2);

        //when
        List<GameExperienceDto> result = experienceCachingService.cacheAllExperiences();

        //then
        assertAll("cacheAllExperiences mock entities assertion set:",
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "Successful " +
                        "cacheAllExperiences should not produce any error logs."),
                () -> assertNotNull(result, "cacheAllExperiences should not return null"),
                () -> assertEquals(2, result.size(), "cacheAllExperiences should return 2 mock DTOs"),
                () -> assertEquals(1, result.getFirst().getExperienceId(), "cacheAllExperiences " +
                        "produced dto with incorrect id"),
                () -> assertEquals("Test Exp1", result.getFirst().getExperienceType(),
                        "cacheAllExperiences produced dto with incorrect type"),
                () -> assertEquals(2, result.get(1).getExperienceId(), "cacheAllExperiences produced" +
                        " dto with incorrect id"),
                () -> assertEquals("Test Exp2", result.get(1).getExperienceType(),
                        "cacheAllExperiences produced dto with incorrect type"),
                () -> verify(experienceRepository, times(1)).findAll());
    }

    @Test
    void testCacheAllExperiences_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(ExperienceCachingServiceImpl.class);
        //given: experiences repo is empty

        //when
        //then
        assertAll("cacheAllExperiences = empty assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        experienceCachingService.cacheAllExperiences()),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Unable to access Experience data for caching."))),
                () -> verify(experienceRepository, times(1)).findAll());
    }
}