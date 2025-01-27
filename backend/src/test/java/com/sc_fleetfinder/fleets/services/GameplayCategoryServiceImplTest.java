package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameplayCategoryServiceImplTest {

    @Mock
    private GameplayCategoryRepository gameplayCategoryRepository;

    @InjectMocks
    private GameplayCategoryServiceImpl gameplayCategoryService;

    @Test
    void testGetAllCategories() {
        //given
        GameplayCategory mockGameplayCategory = new GameplayCategory();
        GameplayCategory mockGameplayCategory2 = new GameplayCategory();
        List<GameplayCategory> mockEntities = List.of(mockGameplayCategory, mockGameplayCategory2);
        when(gameplayCategoryRepository.findAll()).thenReturn(mockEntities);

        //when
        List<GameplayCategoryDto> result = gameplayCategoryService.getAllCategories();

        //then
        assertAll("get all categories mock entities assertions set:",
                () -> assertNotNull(result, "get all categories should not be null here"),
                () -> assertEquals(2, result.size(), "get all categories should have 2 elements here"),
                () -> verify(gameplayCategoryRepository, times(1)).findAll());
    }

    @Test
    void testGetAllCategoriesNotFound() {
        //given
        when(gameplayCategoryRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<GameplayCategoryDto> result = gameplayCategoryService.getAllCategories();

        //then
        assertAll("get all experiences = empty assertions set:",
                () -> assertNotNull(result, "get all categories should not be null"),
                () -> assertTrue(result.isEmpty(), "get all categories should be empty here"),
                () -> verify(gameplayCategoryRepository, times(1)).findAll());
    }

    @Test
    void testGetCategoryById_Found() {
        //given
        GameplayCategory mockGameplayCategory = new GameplayCategory();
        when(gameplayCategoryRepository.findById(1)).thenReturn(Optional.of(mockGameplayCategory));

        //when
        GameplayCategoryDto result = gameplayCategoryService.getCategoryById(1);

        //then
        assertAll("Get category by Id=Found assertions set:",
                () -> assertNotNull(result, "Found categoryId should not return null DTO"),
                () -> assertDoesNotThrow(() -> gameplayCategoryService.getCategoryById(1),
                        "getCategoryById should not throw exception when Id is found"),
                () -> verify(gameplayCategoryRepository, times(2)).findById(1));
    }

    @Test
    void testGetCategoryById_NotFound() {
        //given
        when(gameplayCategoryRepository.findById(1)).thenReturn(Optional.empty());

        //when categoryRepository does not contain category with given id

        //then
        assertThrows(ResourceNotFoundException.class, () -> gameplayCategoryService.getCategoryById(1),
                "getCategoryById with id not found should throw exception");
    }

    @Test
    void testConvertToDto() {
        //given
        GameplayCategory mockGameplayCategory = new GameplayCategory();
        mockGameplayCategory.setCategoryId(1);
        mockGameplayCategory.setCategoryName("Test Category1");
        GameplayCategory mockGameplayCategory2 = new GameplayCategory();
        mockGameplayCategory2.setCategoryId(2);
        mockGameplayCategory2.setCategoryName("Test Category2");

        List<GameplayCategory> mockEntities = List.of(mockGameplayCategory, mockGameplayCategory2);
        when(gameplayCategoryRepository.findAll()).thenReturn(mockEntities);

        //when
        List<GameplayCategoryDto> result = gameplayCategoryService.getAllCategories();

        //then
        assertAll("convert category entity to DTO assertions set",
                () -> assertNotNull(result, "convert category entity to DTO should not return null"),
                () -> assertEquals(2, result.size(), "test category convertToDto should produce" +
                        "2 elements"),
                () -> assertEquals(1, result.getFirst().getGameplayCategoryId(), "convert category" +
                        " entity to DTO Id's do not match"),
                () -> assertEquals("Test Category1", result.getFirst().getGameplayCategoryName(),
                        "convert category entity to DTO categoryNames do not match"),
                () -> assertEquals(2, result.get(1).getGameplayCategoryId(), "convert category" +
                        " entity to DTO Id's do not match"),
                () -> assertEquals("Test Category2", result.get(1).getGameplayCategoryName(),
                        "convert category entity to DTO categoryNames do not match"),
                () -> verify(gameplayCategoryRepository, times(1)).findAll());
    }

    @Test
    void testConvertToEntity_Found() {
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
        GameplayCategory mockEntity = gameplayCategoryService.convertToEntity(mockCategoryDto);

        //then
        assertAll("gameplayCategory convertToEntity assertions set:",
                () -> assertNotNull(mockGameplayCategory, "gameplayCategory convertToEntity should " +
                        "not return null"),
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
        //given
        GameplayCategoryDto mockCategoryDto = new GameplayCategoryDto();
        mockCategoryDto.setGameplayCategoryId(1);
        mockCategoryDto.setGameplayCategoryName("Not Category1");
        when(gameplayCategoryRepository.findById(1)).thenReturn(Optional.empty());

        //when backend entity does not exist with dto ID

        //then
        assertThrows(ResourceNotFoundException.class, () -> gameplayCategoryService.convertToEntity(mockCategoryDto),
                "convertToEntity with id not found should throw exception");
    }

    @Test
    void testConvertToEntity_NameMismatch() {
        //given
        GameplayCategoryDto mockCategoryDto = new GameplayCategoryDto();
        mockCategoryDto.setGameplayCategoryId(1);
        mockCategoryDto.setGameplayCategoryName("Wrong Category");

        GameplayCategory mockCategory = new GameplayCategory();
        mockCategory.setCategoryId(1);
        mockCategory.setCategoryName("Correct Category");

        when(gameplayCategoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));

        //when Dto id and name do not match entity id and name

        //then
        assertThrows(ResourceNotFoundException.class, () ->
            gameplayCategoryService.convertToEntity(mockCategoryDto),
            "convertToEntity with id/name mismatch should throw exception");
    }
}