package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.SubcategoryCachingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameplaySubcategoryServiceImplTest {

    @Mock
    private GameplaySubcategoryRepository gameplaySubcategoryRepository;

    @Mock
    private SubcategoryCachingServiceImpl subcategoryCachingService;

    @InjectMocks
    private GameplaySubcategoryServiceImpl gameplaySubcategoryService;

    @Test
    void testGetAllSubcategories_Found() {
        //given
        GameplaySubcategoryDto mockDto1 = new GameplaySubcategoryDto();
        GameplaySubcategoryDto mockDto2 = new GameplaySubcategoryDto();
        List<GameplaySubcategoryDto> mockDtoes = List.of(mockDto1, mockDto2);
        when(subcategoryCachingService.cacheAllSubcategories()).thenReturn(mockDtoes);

        //when
        List<GameplaySubcategoryDto> result = gameplaySubcategoryService.getAllSubcategories();

        //then
        assertAll("get all subcategories mock entities assertions set:",
                () -> assertNotNull(result, "get all subcategories should not return null"),
                () -> assertEquals(2, result.size(), "get all subcategories should contain 2 elements."),
                () -> verify(subcategoryCachingService, times(1)).cacheAllSubcategories());
    }

    @Test
    void testGetAllSubcategories_NotFound() {
        //given subcategories repo is empty

        //telling the test to throw an exception when it tries to cache an empty repo
        when(subcategoryCachingService.cacheAllSubcategories()).thenThrow(ResourceNotFoundException.class);

        //when
        //then
        assertAll("get all subcategories = empty assertions set:",
                () -> assertThrows(ResourceNotFoundException.class,
                        () -> gameplaySubcategoryService.getAllSubcategories()),
                () -> verify(subcategoryCachingService, times(1)).cacheAllSubcategories());
    }

    @Test
    void testGetSubcategoryById_Found() {
        //given
        GameplaySubcategoryDto mockDto = new GameplaySubcategoryDto();
            mockDto.setSubcategoryId(1);
            mockDto.setSubcategoryName("Test1");
            mockDto.setGameplayCategoryName("mockCat");
        GameplaySubcategoryDto mockDto2 = new GameplaySubcategoryDto();
            mockDto2.setSubcategoryId(2);
            mockDto2.setSubcategoryName("Test2");
            mockDto2.setGameplayCategoryName("mockCat");
        List<GameplaySubcategoryDto> mockDtoes = List.of(mockDto, mockDto2);
        when(subcategoryCachingService.cacheAllSubcategories()).thenReturn(mockDtoes);

        GameplayCategory mockCategory = new GameplayCategory();
            mockCategory.setCategoryId(1);
            mockCategory.setCategoryName("mockCat");
            mockCategory.setGameplaySubcategories(Set.of(new GameplaySubcategory(), new GameplaySubcategory()));

        GameplaySubcategory mockEntity = new GameplaySubcategory();
            mockEntity.setSubcategoryId(1);
            mockEntity.setSubcategoryName("Test1");
            mockEntity.setGameplayCategory(mockCategory);

        //when
        GameplaySubcategoryDto result = gameplaySubcategoryService.getSubcategoryById(1);

        //then
        assertAll("Get subcategory by Id=found assertions set:",
                () -> assertNotNull(result, "Found subcategoryId should not return null DTO"),
                () -> assertDoesNotThrow(() -> gameplaySubcategoryService.getSubcategoryById(1),
                        "getSubcategoryById should not throw exception when Id is found"),
                () -> verify(subcategoryCachingService, times(2)).cacheAllSubcategories());
    }

    @Test
    void testGetSubcategoryById_NotFound() {
        //given: subcategory repository does not contain subcategory with given id

        //when
        //then
        assertAll("getSubcategoryById = not found assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> gameplaySubcategoryService.getSubcategoryById(1),
                "getSubcategoryById with id not found should throw exception"),
                () -> verify(subcategoryCachingService, times(1)).cacheAllSubcategories());

    }

    @Test
    void testConvertToDto_Success() {
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


        //when
        List<GameplaySubcategoryDto> result = List.of(gameplaySubcategoryService.convertToDto(mockGameplaySubcategory1),
                gameplaySubcategoryService.convertToDto(mockGameplaySubcategory2));

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
                        "subcategory entity to DTO parent-categoryNames deo not match"));
    }

    @Test
    void testConvertToEntity_Found() {
        //given
        //creating a mock subcategory entity to add to mock gameplay category entity set of subcategory entities
        GameplaySubcategory mockGameplaySubcategory1 = new GameplaySubcategory();
            mockGameplaySubcategory1.setSubcategoryId(1);
            mockGameplaySubcategory1.setSubcategoryName("Test Subcategory1");

        //creating mock gameplay category to allow conversion of subcategory.getGameplayCategoryName conversion to entity
        GameplayCategory mockGameplayCategory = new GameplayCategory();
            mockGameplayCategory.setCategoryId(1);
            mockGameplayCategory.setCategoryName("Test Category");
            mockGameplayCategory.getGameplaySubcategories().add(mockGameplaySubcategory1);

        //setting category for mock subcategory so that I can use assertSame() to test the converted entity's category
        mockGameplaySubcategory1.setGameplayCategory(mockGameplayCategory);

        //GameplaySubcategoryDto for conversion to entity
        GameplaySubcategoryDto mockGameplaySubcategoryDto = new GameplaySubcategoryDto();
            mockGameplaySubcategoryDto.setSubcategoryId(1);
            mockGameplaySubcategoryDto.setSubcategoryName("Test Subcategory1");
            mockGameplaySubcategoryDto.setGameplayCategoryName(mockGameplayCategory.getCategoryName());
        when(gameplaySubcategoryRepository.findById(1)).thenReturn(Optional.of(mockGameplaySubcategory1));

        //when
        GameplaySubcategory mockEntity = gameplaySubcategoryService.convertToEntity(mockGameplaySubcategoryDto);

        //then
        assertAll("subcategory convertToEntity assertions set:",
                () -> assertNotNull(mockEntity, "subcategory convertToEntity should not return null"),
                () -> assertEquals(1, mockEntity.getSubcategoryId(), "subcategory convertToEntity " +
                        "Id's do not match"),
                () -> assertEquals("Test Subcategory1", mockEntity.getSubcategoryName(), "subcategory " +
                        "convertToEntity subcategory names do not match"),
                () -> assertSame(mockGameplaySubcategory1, mockEntity, "Converted entity with ID: "
                        + mockEntity.getSubcategoryId() + " and subcategoryName " + mockEntity.getSubcategoryName()
                        + " does not match ID: " + mockGameplaySubcategory1.getSubcategoryId()));
    }

    @Test
    void testConvertToEntity_NotFound() {
        //given
        GameplaySubcategoryDto mockGameplaySubcategoryDto = new GameplaySubcategoryDto();
            mockGameplaySubcategoryDto.setSubcategoryId(1);
            mockGameplaySubcategoryDto.setSubcategoryName("Not Subcategory1");
        when(gameplaySubcategoryRepository.findById(1)).thenReturn(Optional.empty());

        //when backend entity does not exist with dto ID

        //then
        assertThrows(ResourceNotFoundException.class, () ->
                gameplaySubcategoryService.convertToEntity(mockGameplaySubcategoryDto),
                "convertToEntity with id not found should throw exception");
    }

    @Test
    void testConvertToEntity_NameMismatch() {
        //given
        GameplaySubcategoryDto mockGameplaySubcategoryDto = new GameplaySubcategoryDto();
            mockGameplaySubcategoryDto.setSubcategoryId(1);
            mockGameplaySubcategoryDto.setSubcategoryName("Wrong Subcategory1");

        GameplaySubcategory mockSubcategory = new GameplaySubcategory();
            mockSubcategory.setSubcategoryId(1);
            mockSubcategory.setSubcategoryName("Correct Subcategory2");

        when(gameplaySubcategoryRepository.findById(1)).thenReturn(Optional.of(mockSubcategory));

        //when Dto id and name do not match entity id and name

        //then
        assertThrows(ResourceNotFoundException.class, () ->
                gameplaySubcategoryService.convertToEntity(mockGameplaySubcategoryDto),
                "convertToEntity with id/name mismatch should throw exception");
    }
}