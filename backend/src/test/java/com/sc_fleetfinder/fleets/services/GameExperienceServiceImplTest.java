package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameExperienceServiceImplTest {

    @Mock
    //@AfterEach MockitoExtension @Mock resets repo
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private GameExperienceServiceImpl gameExperienceService;

    @Test
    void testGetAllExperiences() {
        //given
        GameExperience mockEntity = new GameExperience();
        GameExperience mockEntity2 = new GameExperience();
        List<GameExperience> mockEntities = List.of(mockEntity, mockEntity2);
        when(experienceRepository.findAll()).thenReturn(mockEntities);

        //when
        List<GameExperienceDto> result = gameExperienceService.getAllExperiences();

        //then
        assertAll("get all experiences mock entities assertions set:",
                () -> assertNotNull(result, "get all experiences should not be null here"),
                () -> assertEquals(2, result.size(), "get all experiences should have 2 elements"),
                () -> verify(experienceRepository, times(1)).findAll());
    }

    @Test
    void testGetAllExperiencesNotFound() {
        //given
        when(experienceRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<GameExperienceDto> result = gameExperienceService.getAllExperiences();

        //then
        assertAll("get all experiences = empty assertions set: ",
                () -> assertNotNull(result,  "get all experiences should not be null here"),
                () -> assertTrue(result.isEmpty(), "get all experiences should be empty here"),
                () -> verify(experienceRepository, times(1)).findAll());
    }

    @Test
    void testGetExperienceById_Found() {
        //given
        GameExperience mockEntity = new GameExperience();
        when(experienceRepository.findById(1)).thenReturn(Optional.of(mockEntity));

        //when
        GameExperienceDto result = gameExperienceService.getExperienceById(1);

        //then
        assertAll("Get experience by Id=found assertions set:",
                () -> assertNotNull(result, "Found experienceId should not return null DTO"),
                () -> assertDoesNotThrow(() -> gameExperienceService.getExperienceById(1),
                    "getExperienceById should not throw exception when found id"),
                () -> verify(experienceRepository, times(2)).findById(1));
    }

    @Test
    void testGetExperienceById_NotFound() {
        //given
        when(experienceRepository.findById(1)).thenReturn(Optional.empty());

        //when experienceRepository does not contain experience with given id

        //then
        assertThrows(ResourceNotFoundException.class, () -> gameExperienceService.getExperienceById(1),
                "getExperienceById with id not found should throw exception");
    }

    @Test
    void testConvertToDto() {
        //given
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(1);
        mockEntity.setExperienceType("Experience1");
        GameExperience mockEntity2 = new GameExperience();
        mockEntity2.setExperienceId(2);
        mockEntity2.setExperienceType("Experience2");

        List<GameExperience> mockEntities = List.of(mockEntity, mockEntity2);
        when(experienceRepository.findAll()).thenReturn(mockEntities);

        //when
        List<GameExperienceDto> result = gameExperienceService.getAllExperiences();

        //then
        assertAll("convert experience entity to DTO assertion set:",
                () -> assertNotNull(result, "convert experience entity should not return null DTO"),
                () -> assertEquals(2, result.size(), "experience convertToDto should produce 2 " +
                        "elements"),
                () -> assertEquals(1, result.getFirst().getExperienceId(), "convert experience" +
                        " entity to DTO Id's do not match"),
                () -> assertEquals("Experience1", result.getFirst().getExperienceType(), "convert" +
                        "experience entity to DTO experienceTypes do not match"),
                () -> assertEquals(2, result.get(1).getExperienceId(), "convert experience" +
                                " entity to DTO Id's do not match"),
                () -> assertEquals("Experience2", result.get(1).getExperienceType(), "convert" +
                        "experience entity to DTO experienceTypes do not match"),
                () -> verify(experienceRepository, times(1)).findAll());
    }

    @Test
    void testConvertToEntity() {
        //given
        GameExperienceDto mockDto = new GameExperienceDto();
        mockDto.setExperienceId(1);
        mockDto.setExperienceType("Experience1");

        //when
        GameExperience mockEntity = gameExperienceService.convertToEntity(mockDto);

        //then
        assertAll("gameExperience convertToEntity assertions set:",
                () -> assertNotNull(mockEntity, "gameExperience convertToEntity should not return null"),
                () -> assertEquals(1, mockEntity.getExperienceId(), "gameExperience convertToEntity" +
                        " experienceIds do not match"),
                () -> assertEquals("Experience1", mockEntity.getExperienceType(), "gameExperience " +
                        "convertToEntity experienceTypes do not match"));
    }
}
