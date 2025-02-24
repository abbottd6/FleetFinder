package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.ExperienceCachingServiceImpl;
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
public class GameExperienceServiceImplTest {

    @Mock
    //@AfterEach MockitoExtension @Mock resets repo
    private ExperienceRepository experienceRepository;

    @Mock
    private ExperienceCachingServiceImpl experienceCachingService;

    @InjectMocks
    private GameExperienceServiceImpl gameExperienceService;

    @Test
    void testGetAllExperiences_Found() {
        //given
        GameExperienceDto mockDto1 = new GameExperienceDto();
        GameExperienceDto mockDto2 = new GameExperienceDto();
        List<GameExperienceDto> mockDtoes = List.of(mockDto1, mockDto2);
        when(experienceCachingService.cacheAllExperiences()).thenReturn(mockDtoes);

        //when
        List<GameExperienceDto> result = gameExperienceService.getAllExperiences();

        //then
        assertAll("getAllExperiences mock entities assertion set:",
                () -> assertNotNull(result, "getAllExperiences should not return null"),
                () -> assertEquals(2, result.size(), "getAllExperiences should have 2 mock Dtoes"),
                () -> verify(experienceCachingService, times(1)).cacheAllExperiences());
    }

    @Test
    void testGetAllExperiences_NotFound() {
        //given: experiences repo is empty

        //telling test to throw an exception when it tries to cache an empty repo
        when(experienceCachingService.cacheAllExperiences()).thenThrow(ResourceNotFoundException.class);

        //when
        //then
        assertAll("getAllExperiences = empty assertion set: ",
                //verifying that the exception propagates from the caching service
                () -> assertThrows(ResourceNotFoundException.class, () -> gameExperienceService.getAllExperiences()),
                () -> verify(experienceCachingService, times(1)).cacheAllExperiences());
    }

    @Test
    void testGetExperienceById_Found() {
        //given
        GameExperienceDto mockDto1 = new GameExperienceDto();
            mockDto1.setExperienceId(1);
            mockDto1.setExperienceType("Test Exp1");
        GameExperienceDto mockDto2 = new GameExperienceDto();
            mockDto2.setExperienceId(2);
            mockDto2.setExperienceType("Test Exp2");
        List<GameExperienceDto> mockDtoes = List.of(mockDto1, mockDto2);
        when(experienceCachingService.cacheAllExperiences()).thenReturn(mockDtoes);

        //when
        GameExperienceDto result = gameExperienceService.getExperienceById(1);

        //then
        assertAll("getExperienceById = found assertion set:",
                () -> assertNotNull(result, "Found experienceId should not return null DTO"),
                () -> assertDoesNotThrow(() -> gameExperienceService.getExperienceById(1),
                    "getExperienceById should not throw exception when Id is found"),
                () -> verify(experienceCachingService, times(2)).cacheAllExperiences());
    }

    @Test
    void testGetExperienceById_NotFound() {
        //given: experienceRepository does not contain experience with given id

        //when
        //then
        assertAll("getExperienceById = not found assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> gameExperienceService.getExperienceById(1),
                        "getExperienceById with id not found should throw exception"),
                () -> verify(experienceCachingService, times(1)).cacheAllExperiences());
    }
}
