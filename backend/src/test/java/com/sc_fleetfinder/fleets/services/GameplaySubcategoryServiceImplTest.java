package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import org.junit.jupiter.api.AfterAll;
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
class GameplaySubcategoryServiceImplTest {

    @Mock
    private GameplaySubcategoryRepository gameplaySubcategoryRepository;

    @Mock
    GameplayCategoryRepository gameplayCategoryRepository;

    @InjectMocks
    private GameplaySubcategoryServiceImpl gameplaySubcategoryService;

    @Test
    void testGetAllSubcategories() {
        //given
        GameplaySubcategory mockGameplaySubcategory1 = new GameplaySubcategory();
        GameplaySubcategory mockGameplaySubcategory2 = new GameplaySubcategory();
        List<GameplaySubcategory> mockEntities = List.of(mockGameplaySubcategory1, mockGameplaySubcategory2);
        when(gameplaySubcategoryRepository.findAll()).thenReturn(mockEntities);

        //when
        List<GameplaySubcategoryDto> result = gameplaySubcategoryService.getAllSubcategories();

        //then
        assertAll("get all subcategories mock entities assertions set:",
                () -> assertNotNull(result, "get all subcategories should not return null"),
                () -> assertEquals(2, result.size(), "get all subcategories should contain 2 elements."),
                () -> verify(gameplaySubcategoryRepository, times(1)).findAll());
    }

    @Test
    void testGetAllSubcategoriesNotFound() {
        //given
        when(gameplaySubcategoryRepository.findAll()).thenReturn(Collections.emptyList());

        //when
        List<GameplaySubcategoryDto> result = gameplaySubcategoryService.getAllSubcategories();

        //then
        assertAll("get all subcategories = empty assertions set:",
                () -> assertNotNull(result, "get all subcategories should not return null"),
                () -> assertTrue(result.isEmpty(), "get all subcategories should return empty here"),
                () -> verify(gameplaySubcategoryRepository, times(1)).findAll());
    }

    @Test
    void testGetSubcategoryById_Found() {
        //given
        GameplaySubcategory mockGameplaySubcategory = new GameplaySubcategory();
        when(gameplaySubcategoryRepository.findById(1)).thenReturn(Optional.of(mockGameplaySubcategory));

        //when
        GameplaySubcategoryDto result = gameplaySubcategoryService.getSubcategoryById(1);

        //then
        assertAll("Get subcategory by Id=found assertions set:",
                () -> assertNotNull(result, "Found subcategoryId should not return null DTO"),
                () -> assertDoesNotThrow(() -> gameplaySubcategoryService.getSubcategoryById(1),
                        "getSubcategoryById should not throw exception when Id is found"),
                () -> verify(gameplaySubcategoryRepository, times(2)).findById(1));
    }

    @Test
    void testGetSubcategoryById_NotFound() {
        //given
        when(gameplaySubcategoryRepository.findById(1)).thenReturn(Optional.empty());

        //when subcategory repository does not contain subcategory with given id

        //then
        assertThrows(ResourceNotFoundException.class, () -> gameplaySubcategoryService.getSubcategoryById(1),
                "getSubcategoryById with id not found should throw exception");

    }

    @Test
    void testConvertToDto() {
        //given
        GameplayCategory mockGameplayCategory = new GameplayCategory();
        mockGameplayCategory.setCategoryId(1);
        mockGameplayCategory.setCategoryName("Test Category");

        GameplaySubcategory mockGameplaySubcategory1 = new GameplaySubcategory();
        mockGameplaySubcategory1.setSubcategoryId(1);
        mockGameplaySubcategory1.setSubcategoryName("Test Subcategory1");
        mockGameplaySubcategory1.setGameplayCategory(mockGameplayCategory);
        GameplaySubcategory mockGameplaySubcategory2 = new GameplaySubcategory();
        mockGameplaySubcategory2.setSubcategoryId(2);
        mockGameplaySubcategory2.setSubcategoryName("Test Subcategory2");
        mockGameplaySubcategory2.setGameplayCategory(mockGameplayCategory);

        mockGameplayCategory.getGameplaySubcategories().addAll(List.of(mockGameplaySubcategory1, mockGameplaySubcategory2));

        List<GameplaySubcategory> mockEntities = List.of(mockGameplaySubcategory1, mockGameplaySubcategory2);
        when(gameplaySubcategoryRepository.findAll()).thenReturn(mockEntities);

        //when
        List<GameplaySubcategoryDto> result = gameplaySubcategoryService.getAllSubcategories();

        //then
        assertAll("convert subcategory entity to DTO assertion set:",
                () -> assertNotNull(result, "convert subcategory entity to DTO should not return null"),
                () -> assertEquals(2, result.size(), "test subcategory convertToDto should produce" +
                        "2 elements"),
                () -> assertEquals(1, result.getFirst().getSubcategoryId(), "convert subcategory " +
                        "entity to DTO Id's do not match"),
                () -> assertEquals("Test Subcategory1", result.getFirst().getSubcategoryName(),
                        "convert subcategory entity to DTO subcategoryNames do not match"),
                () -> assertEquals("Test Category", result.getFirst().getGameplayCategoryName(),
                        "convert subcategory entity to DTO parent-categoryNames do not match"),
                () -> assertEquals(2, result.get(1).getSubcategoryId(), "convert subcategory " +
                        "entity to DTO Id's do not match"),
                () -> assertEquals("Test Subcategory2", result.get(1).getSubcategoryName(), "convert " +
                        "subcategory entity to DTO subcategoryNames do not match"),
                () -> assertEquals("Test Category", result.get(1).getGameplayCategoryName(), "convert " +
                        "subcategory entity to DTO parent-categoryNames deo not match"),
                () -> verify(gameplaySubcategoryRepository, times(1)).findAll());
    }

    @Test
    void testConvertToEntity() {
        //given
        //creating a mock subcategory entity to add to mock gameplay category entity set of subcategory entities
        GameplaySubcategory mockGameplaySubcategory2 = new GameplaySubcategory();
        mockGameplaySubcategory2.setSubcategoryId(2);
        mockGameplaySubcategory2.setSubcategoryName("Test Subcategory2");

        //creating mock gameplay category to allow conversion of subcategory.getGameplayCategoryName conversion to entity
        GameplayCategory mockGameplayCategory = new GameplayCategory();
        mockGameplayCategory.setCategoryId(1);
        mockGameplayCategory.setCategoryName("Test Category");
        mockGameplayCategory.getGameplaySubcategories().add(mockGameplaySubcategory2);

        //setting category for mock subcategory so that I can use assertSame() to test the converted entity's category
        mockGameplaySubcategory2.setGameplayCategory(mockGameplayCategory);

        //GameplaySubcategoryDto for conversion to entity
        GameplaySubcategoryDto mockGameplaySubcategoryDto = new GameplaySubcategoryDto();
        mockGameplaySubcategoryDto.setSubcategoryId(1);
        mockGameplaySubcategoryDto.setSubcategoryName("Mock Subcategory");
        mockGameplaySubcategoryDto.setGameplayCategoryName(mockGameplayCategory.getCategoryName());

        when(gameplayCategoryRepository.findByName(mockGameplaySubcategoryDto.getGameplayCategoryName()))
                .thenReturn(Optional.of(mockGameplayCategory));

        //when
        GameplaySubcategory mockEntity = gameplaySubcategoryService.convertToEntity(mockGameplaySubcategoryDto);

        //then
        assertAll("subcategory convertToEntity assertions set:",
                () -> assertNotNull(mockEntity, "subcategory convertToEntity should not return null"),
                () -> assertEquals(1, mockEntity.getSubcategoryId(), "subcategory converToEntity " +
                        "Id's do not match"),
                () -> assertEquals("Mock Subcategory", mockEntity.getSubcategoryName(), "subcategory " +
                        "convertToEntity subcategory names do not match"),
                () -> assertSame(mockGameplayCategory, mockEntity.getGameplayCategory(), "subcategory" +
                        "convertToEntity parent-categoryNames do not match"));
    }
}