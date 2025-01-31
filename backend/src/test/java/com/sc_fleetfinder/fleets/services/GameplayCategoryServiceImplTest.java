package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.CategoryCachingServiceImpl;
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
class GameplayCategoryServiceImplTest {

    @Mock
    private GameplayCategoryRepository gameplayCategoryRepository;

    @Mock
    private CategoryCachingServiceImpl categoryCachingService;

    @InjectMocks
    private GameplayCategoryServiceImpl gameplayCategoryService;

    @Test
    void testGetAllCategories_Found() {
        //given
        GameplayCategoryDto mockDto1 = new GameplayCategoryDto();
            mockDto1.setGameplayCategoryId(1);
            mockDto1.setGameplayCategoryName("Test Category");
        GameplayCategoryDto mockDto2 = new GameplayCategoryDto();
            mockDto2.setGameplayCategoryId(2);
            mockDto2.setGameplayCategoryName("Test Category2");
        List<GameplayCategoryDto> mockDtoes = List.of(mockDto1, mockDto2);
        when(categoryCachingService.cacheAllCategories()).thenReturn(mockDtoes);

        //when
        List<GameplayCategoryDto> result = gameplayCategoryService.getAllCategories();

        //then
        assertAll("get all categories mock entities assertions set:",
                () -> assertNotNull(result, "get all categories should not be null here"),
                () -> assertEquals(2, result.size(), "get all categories should have 2 elements here"),
                () -> verify(categoryCachingService, times(1)).cacheAllCategories());
    }

    @Test
    void testGetAllCategories_NotFound() {
        //given categories repo is empty

        //telling test to throw an exception when it tries to cache an empty repo
        when(categoryCachingService.cacheAllCategories()).thenThrow(ResourceNotFoundException.class);

        //when
        //then
        assertAll("getAllCategories = empty assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> gameplayCategoryService.getAllCategories()),
                () -> verify(categoryCachingService, times(1)).cacheAllCategories());
    }

    @Test
    void testGetCategoryById_Found() {
        //given
        GameplayCategoryDto mockDto1 = new GameplayCategoryDto();
            mockDto1.setGameplayCategoryId(1);
            mockDto1.setGameplayCategoryName("Test Category");
        GameplayCategoryDto mockDto2 = new GameplayCategoryDto();
            mockDto2.setGameplayCategoryId(2);
            mockDto2.setGameplayCategoryName("Test Category2");
        List<GameplayCategoryDto> mockDtoes = List.of(mockDto1, mockDto2);
        when(categoryCachingService.cacheAllCategories()).thenReturn(mockDtoes);

        //when
        GameplayCategoryDto result = gameplayCategoryService.getCategoryById(1);

        //then
        assertAll("Get category by Id=Found assertions set:",
                () -> assertNotNull(result, "Found categoryId should not return null DTO"),
                () -> assertDoesNotThrow(() -> gameplayCategoryService.getCategoryById(1),
                        "getCategoryById should not throw exception when Id is found"),
                () -> verify(categoryCachingService, times(2)).cacheAllCategories());
    }

    @Test
    void testGetCategoryById_NotFound() {
        //given categoryRepository does not contain category with given id

        //when
        //then
        assertAll("getCategoryById = not found assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> gameplayCategoryService.getCategoryById(1),
                "getCategoryById with id not found should throw exception"),
                () -> verify(categoryCachingService, times(1)).cacheAllCategories());
    }

    @Test
    void testConvertToDto_Success() {
        //given
        GameplayCategory mockEntity1 = new GameplayCategory();
            mockEntity1.setCategoryId(1);
            mockEntity1.setCategoryName("Test Category1");
        GameplayCategory mockEntity2 = new GameplayCategory();
            mockEntity2.setCategoryId(2);
            mockEntity2.setCategoryName("Test Category2");

        //when
        List<GameplayCategoryDto> result = List.of(gameplayCategoryService.convertToDto(mockEntity1),
                gameplayCategoryService.convertToDto(mockEntity2));

        //then
        assertAll("convert category entity to DTO assertions set",
                () -> assertNotNull(result, "convert category entity to DTO should not return null"),
                () -> assertDoesNotThrow(() -> gameplayCategoryService.convertToDto(mockEntity1),
                        "convert Category entity to Dto should NOT throw an exception when fields are valid"),
                () -> assertDoesNotThrow(() -> gameplayCategoryService.convertToDto(mockEntity2),
                        "convert Category entity to Dto should NOT throw an exception when fields are valid"),
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
        //given: entity with a null id value (invalid id)
        GameplayCategory mockCategory = new GameplayCategory();
            mockCategory.setCategoryId(null);
            mockCategory.setCategoryName("Test Category1");

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> gameplayCategoryService.convertToDto(mockCategory),
                "null CategoryId for entity to Dto conversion should throw exception" );
    }

    @Test
    void testConvertToDtoMapping_FailIdZero() {
        //given: entity with an id value of 0 (invalid)
        GameplayCategory mockCategory = new GameplayCategory();
            mockCategory.setCategoryId(0);
            mockCategory.setCategoryName("Test Category1");

        //when
        //then
        assertThrows(ResourceNotFoundException.class, () -> gameplayCategoryService.convertToDto(mockCategory),
                "null CategoryId for entity to Dto conversion should throw exception" );
    }

    @Test
    void testConvertToDtoMapping_FailureNullName() {
        //given: an en
        GameplayCategory mockCategory = new GameplayCategory();
            mockCategory.setCategoryId(1);
            mockCategory.setCategoryName(null);

        assertThrows(ResourceNotFoundException.class, () -> gameplayCategoryService.convertToDto(mockCategory),
                "null CategoryName for entity to Dto conversion should throw exception" );
    }

    @Test
    void testConvertToDtoMapping_FailureEmptyName() {
        GameplayCategory mockCategory = new GameplayCategory();
            mockCategory.setCategoryId(1);
            mockCategory.setCategoryName("");

        assertThrows(ResourceNotFoundException.class, () -> gameplayCategoryService.convertToDto(mockCategory),
                "Empty CategoryName for entity to Dto conversion should throw exception" );
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
                () -> assertDoesNotThrow(() -> gameplayCategoryService.convertToEntity(mockCategoryDto)),
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

        when(gameplayCategoryRepository.findById(mockCategoryDto.getGameplayCategoryId())).thenReturn(Optional.of(mockCategory));

        //when Dto id and name do not match entity id and name

        //then
        assertThrows(ResourceNotFoundException.class, () ->
            gameplayCategoryService.convertToEntity(mockCategoryDto),
            "convertToEntity with id/name mismatch should throw exception");
    }
}