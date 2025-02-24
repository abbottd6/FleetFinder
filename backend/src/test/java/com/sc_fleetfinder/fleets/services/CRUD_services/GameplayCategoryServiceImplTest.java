package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.CategoryCachingServiceImpl;
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
class GameplayCategoryServiceImplTest {

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
}