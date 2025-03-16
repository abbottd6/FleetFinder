package com.sc_fleetfinder.fleets.unit_tests.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GameplaySubcategoryServiceImpl;
import com.sc_fleetfinder.fleets.services.caching_services.SubcategoryCachingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameplaySubcategoryServiceImplTest {

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
}