package com.sc_fleetfinder.fleets.unit_tests.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.GameplayCategoryConversionServiceImpl;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameplayCategoryConversionServiceImplTest {

    @InjectMocks
    private GameplayCategoryConversionServiceImpl gameplayCategoryConversionService;

    @Mock
    private GameplayCategoryRepository gameplayCategoryRepository;

    @Test
    void testConvertToDto_Success() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplayCategoryConversionServiceImpl.class);
        //given
        GameplayCategory mockEntity1 = new GameplayCategory();
        mockEntity1.setCategoryId(1);
        mockEntity1.setCategoryName("Test Category1");
        GameplayCategory mockEntity2 = new GameplayCategory();
        mockEntity2.setCategoryId(2);
        mockEntity2.setCategoryName("Test Category2");

        //when
        List<GameplayCategoryDto> result = List.of(gameplayCategoryConversionService.convertToDto(mockEntity1),
                gameplayCategoryConversionService.convertToDto(mockEntity2));

        //then
        assertAll("convert category entity to DTO assertions set",
                () -> assertNotNull(result, "convert category entity to DTO should not return null"),
                () -> assertDoesNotThrow(() -> gameplayCategoryConversionService.convertToDto(mockEntity1),
                        "convert Category entity to Dto should NOT throw an exception when fields are valid"),
                () -> assertDoesNotThrow(() -> gameplayCategoryConversionService.convertToDto(mockEntity2),
                        "convert Category entity to Dto should NOT throw an exception when fields are valid"),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "Successful category " +
                        "convertToDto should not produce any error logs."),
                () -> assertEquals(2, result.size(), "test category convertToDto should produce" +
                        "2 elements"),
                () -> assertEquals(1, result.getFirst().getGameplayCategoryId(), "convert category" +
                        " entity to DTO Id's do not match"),
                () -> assertEquals("Test Category1", result.getFirst().getGameplayCategoryName(),
                        "convert category entity to DTO categoryNames do not match"),
                () -> assertEquals(2, result.get(1).getGameplayCategoryId(), "convert category" +
                        " entity to DTO Id's do not match"),
                () -> assertEquals("Test Category2", result.get(1).getGameplayCategoryName(),
                        "convert category entity to DTO categoryNames do not match"));
    }

    @Test
    void testConvertToDtoMapping_FailNullId() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplayCategoryConversionServiceImpl.class);
        //given: entity with a null id value (invalid id)
        GameplayCategory mockCategory = new GameplayCategory();
        mockCategory.setCategoryId(null);
        mockCategory.setCategoryName("Test Category1");

        //when
        //then
        assertAll("Category convertToDtoMapping_FailNullId assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () -> gameplayCategoryConversionService.convertToDto(mockCategory),
                "null CategoryId for entity to Dto conversion should throw exception" ),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Category convertToDto encountered an Id that is null" +
                                " or zero."))));
    }

    @Test
    void testConvertToDtoMapping_FailIdZero() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplayCategoryConversionServiceImpl.class);
        //given: entity with an id value of 0 (invalid)
        GameplayCategory mockCategory = new GameplayCategory();
        mockCategory.setCategoryId(0);
        mockCategory.setCategoryName("Test Category1");

        //when
        //then
        assertAll("Category convertToDtoMapping_FailIdZero assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () -> gameplayCategoryConversionService.convertToDto(mockCategory),
                "null CategoryId for entity to Dto conversion should throw exception" ),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Category convertToDto encountered an Id that is null" +
                                " or zero."))));
    }

    @Test
    void testConvertToDtoMapping_FailureNullName() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplayCategoryConversionServiceImpl.class);
        //given: an entity with a null category name
        GameplayCategory mockCategory = new GameplayCategory();
        mockCategory.setCategoryId(1);
        mockCategory.setCategoryName(null);

        //when
        //then
        assertAll("Category convertToDtoMapping_FailureNullName assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () -> gameplayCategoryConversionService.convertToDto(mockCategory),
                "null CategoryName for entity to Dto conversion should throw exception" ),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Category convertToDto encountered an Name that is null " +
                                "or empty."))));
    }

    @Test
    void testConvertToDtoMapping_FailureEmptyName() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplayCategoryConversionServiceImpl.class);
        //given: an entity with an empty string for category name
        GameplayCategory mockCategory = new GameplayCategory();
        mockCategory.setCategoryId(1);
        mockCategory.setCategoryName("");

        //when
        //then
        assertAll("Category convertToDtoMapping_FailureEmptyName assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () -> gameplayCategoryConversionService.convertToDto(mockCategory),
                "Empty CategoryName for entity to Dto conversion should throw exception" ),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Category convertToDto encountered an Name that is null " +
                                "or empty."))));
    }

    @Test
    void testConvertToEntity_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplayCategoryConversionServiceImpl.class);
        //given

        //Mock subcategory to add to Category hashset of subcategories
        GameplaySubcategory mockGameplaySubcategory = new GameplaySubcategory();
        mockGameplaySubcategory.setSubcategoryId(1);
        mockGameplaySubcategory.setSubcategoryName("Test Subcategory1");

        //Mock category to test entity equivalence after conversion
        GameplayCategory mockGameplayCategory = new GameplayCategory();
        mockGameplayCategory.setCategoryId(1);
        mockGameplayCategory.setCategoryName("Test Category1");

        //assigning mock Category to mock subcategory and vice versa
        mockGameplaySubcategory.setGameplayCategory(mockGameplayCategory);
        mockGameplayCategory.getGameplaySubcategories().add(mockGameplaySubcategory);

        //mock DTO to convert to entity
        GameplayCategoryDto mockCategoryDto = new GameplayCategoryDto();
        mockCategoryDto.setGameplayCategoryId(1);
        mockCategoryDto.setGameplayCategoryName("Test Category1");
        when(gameplayCategoryRepository.findById(1)).thenReturn(Optional.of(mockGameplayCategory));

        //when
        GameplayCategory mockEntity = gameplayCategoryConversionService.convertToEntity(mockCategoryDto);

        //then
        assertAll("gameplayCategory convertToEntity assertions set:",
                () -> assertNotNull(mockGameplayCategory, "gameplayCategory convertToEntity should " +
                        "not return null"),
                () -> assertDoesNotThrow(() -> gameplayCategoryConversionService.convertToEntity(mockCategoryDto)),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "Successful Category " +
                        "convertToEntity should not produce any error logs."),
                () -> assertEquals(1, mockEntity.getCategoryId(), "gameplayCategory" +
                        " convertToEntity categoryIds do not match"),
                () -> assertEquals("Test Category1", mockEntity.getCategoryName(),
                        "gameplayCategory convertToEntity categoryNames do not match"),
                () -> assertSame(mockGameplayCategory, mockEntity, "Converted entity with ID: "
                        + mockEntity.getCategoryId() + " and categoryName " + mockEntity.getCategoryName()
                        + " does not match ID: " + mockGameplayCategory.getCategoryId()));
    }

    @Test
    void testConvertToEntity_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplayCategoryConversionServiceImpl.class);
        //given a dto that does not exist in the repo
        GameplayCategoryDto mockCategoryDto = new GameplayCategoryDto();
        mockCategoryDto.setGameplayCategoryId(1);
        mockCategoryDto.setGameplayCategoryName("Not Category1");

        //when backend entity does not exist with dto ID

        //then
        assertAll("Category convertToEntity_NotFound assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> gameplayCategoryConversionService.convertToEntity(mockCategoryDto),
                "convertToEntity with id not found should throw exception"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Category convertToEntity could not find entity with Id: 1"))));
    }

    @Test
    void testConvertToEntity_NameMismatch() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplayCategoryConversionServiceImpl.class);
        //given
        GameplayCategoryDto mockCategoryDto = new GameplayCategoryDto();
        mockCategoryDto.setGameplayCategoryId(1);
        mockCategoryDto.setGameplayCategoryName("Wrong Category");

        GameplayCategory mockCategory = new GameplayCategory();
        mockCategory.setCategoryId(1);
        mockCategory.setCategoryName("Correct Category");

        when(gameplayCategoryRepository.findById(mockCategoryDto.getGameplayCategoryId())).thenReturn(Optional.of(mockCategory));

        //when Dto id and name do not match entity id and name

        //then
        assertAll("Category convertToEntity_NameMismatch assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        gameplayCategoryConversionService.convertToEntity(mockCategoryDto),
                "convertToEntity with id/name mismatch should throw exception"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Category convertToEntity encountered an Id/name mismatch " +
                                "for Id: 1, and category: Wrong Category"))));
    }
}