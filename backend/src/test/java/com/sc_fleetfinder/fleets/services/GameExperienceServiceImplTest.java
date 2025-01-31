package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.ExperienceCachingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
                () -> assertNotNull(result, "getAllExperiences should not be null here"),
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

    @Test
    void testConvertToDto_Success() {
        //given
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(1);
        mockEntity.setExperienceType("Experience1");
        GameExperience mockEntity2 = new GameExperience();
        mockEntity2.setExperienceId(2);
        mockEntity2.setExperienceType("Experience2");

        //when
        List<GameExperienceDto> result = List.of(gameExperienceService.convertToDto(mockEntity),
                gameExperienceService.convertToDto(mockEntity2));

        //then
        assertAll("convert experience entity to DTO assertion set:",
                () -> assertNotNull(result, "convert experience entity should not return null DTO"),
                () -> assertDoesNotThrow(() -> gameExperienceService.convertToDto(mockEntity),
                "convert experience entity to DTO should NOT throw an exception when fields are valid"),
                () -> assertDoesNotThrow(() -> gameExperienceService.convertToDto(mockEntity2),
                        "convert experience entity to DTO should NOT throw an exception when fields are valid"),
                () -> assertEquals(2, result.size(), "test experience convertToDto should produce 2 " +
                        "elements"),
                () -> assertEquals(1, result.getFirst().getExperienceId(), "convert experience" +
                        " entity to DTO Id's do not match"),
                () -> assertEquals("Experience1", result.getFirst().getExperienceType(), "convert" +
                        "experience entity to DTO experienceTypes do not match"),
                () -> assertEquals(2, result.get(1).getExperienceId(), "convert experience" +
                                " entity to DTO Id's do not match"),
                () -> assertEquals("Experience2", result.get(1).getExperienceType(), "convert" +
                        "experience entity to DTO experienceTypes do not match"));
    }

    @Test
    void testConvertToDto_FailIdNull() {
        //given
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(null);
        mockEntity.setExperienceType("Experience1");

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> gameExperienceService.convertToDto(mockEntity),
                "convert experience entity to dto should throw exception when id is null");
    }

    @Test
    void testConvertToDto_FailIdZero() {
        //given: an entity with id of 0 (invalid id)
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(0);
        mockEntity.setExperienceType("Experience1");

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> gameExperienceService.convertToDto(mockEntity),
                "convert experience entity to dto should throw exception when id is 0");
    }

    @Test
    void testConvertToDto_FailTypeIsNull() {
        //given: entity with a null type value
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(1);
        mockEntity.setExperienceType(null);

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> gameExperienceService.convertToDto(mockEntity),
                "convert experience entity to dto should throw an exception when type value is null");
    }

    @Test
    void testConvertToDto_FailTypeIsEmpty() {
        //given: entity with an empty string for type
        GameExperience mockEntity = new GameExperience();
        mockEntity.setExperienceId(1);
        mockEntity.setExperienceType("");

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> gameExperienceService.convertToDto(mockEntity),
                "convert experience entity to dto should throw an exception when type " +
                        "value is an empty string");
    }

    @Test
    void testConvertToEntity_Found() {
        //given: a dto that matches an entity
        GameExperienceDto mockDto = new GameExperienceDto();
            mockDto.setExperienceId(1);
            mockDto.setExperienceType("Experience1");
        GameExperience mockEntity = new GameExperience();
            mockEntity.setExperienceId(1);
            mockEntity.setExperienceType("Experience1");

        when(experienceRepository.findById(mockDto.getExperienceId())).thenReturn(Optional.of(mockEntity));

        //when
        GameExperience result = gameExperienceService.convertToEntity(mockDto);

        //then
        assertAll("gameExperience convertToEntity assertions set:",
                () -> assertNotNull(result, "gameExperience convertToEntity should not return null"),
                () -> assertDoesNotThrow(() -> gameExperienceService.convertToEntity(mockDto),
                        "converting valid dto to entity should not throw an exception"),
                () -> assertEquals(1, result.getExperienceId(), "gameExperience convertToEntity" +
                        " experienceIds do not match"),
                () -> assertEquals("Experience1", result.getExperienceType(), "gameExperience " +
                        "convertToEntity experienceTypes do not match"),
                () -> assertSame(mockEntity, result, "convert experience dto to entity does not match " +
                        "target entity"));
    }

    @Test
    void testConvertToEntity_NotFound() {
        //given
        GameExperienceDto mockDto = new GameExperienceDto();
            mockDto.setExperienceId(1);
            mockDto.setExperienceType("Not Experience1");

        //when backend entity does not exist with dto ID

        //then
        assertThrows(ResourceNotFoundException.class, () -> gameExperienceService.convertToEntity(mockDto),
                "convert experience Dto to entity where dto id is not found in repo should throw " +
                        "an exception");
    }

    @Test
    void testConvertToEntity_NameMismatch() {
        //given: dto with a name that does not match the name of the entity with the same id
        GameExperienceDto mockDto = new GameExperienceDto();
            mockDto.setExperienceId(1);
            mockDto.setExperienceType("Incorrect Experience1");

        GameExperience mockEntity = new GameExperience();
            mockEntity.setExperienceId(1);
            mockEntity.setExperienceType("Correct Experience1");

        when(experienceRepository.findById(mockDto.getExperienceId())).thenReturn(Optional.of(mockEntity));

        //then
        assertThrows(ResourceNotFoundException.class, () -> gameExperienceService.convertToEntity(mockDto));

    }
}
