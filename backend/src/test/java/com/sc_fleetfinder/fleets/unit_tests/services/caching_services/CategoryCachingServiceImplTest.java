package com.sc_fleetfinder.fleets.unit_tests.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.CategoryCachingServiceImpl;
import com.sc_fleetfinder.fleets.services.conversion_services.GameplayCategoryConversionServiceImpl;
import nl.altindag.log.LogCaptor;
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
class CategoryCachingServiceImplTest {

    @Mock
    private GameplayCategoryRepository gameplayCategoryRepository;

    @Mock
    private GameplayCategoryConversionServiceImpl gameplayCategoryConversionService;

    @InjectMocks
    private CategoryCachingServiceImpl categoryCachingService;

    @Test
    void testCacheAllCategories_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(CategoryCachingServiceImpl.class);
        //given
        GameplayCategory mockGameplayCategory = new GameplayCategory();
            mockGameplayCategory.setCategoryId(1);
            mockGameplayCategory.setCategoryName("Test Category1");
            mockGameplayCategory.setGameplaySubcategories(Set.of(new GameplaySubcategory(), new GameplaySubcategory()));
        GameplayCategory mockGameplayCategory2 = new GameplayCategory();
            mockGameplayCategory2.setCategoryId(2);
            mockGameplayCategory2.setCategoryName("Test Category2");
            mockGameplayCategory2.setGameplaySubcategories(Set.of(new GameplaySubcategory(), new GameplaySubcategory()));
        List<GameplayCategory> mockEntities = List.of(mockGameplayCategory, mockGameplayCategory2);
        when(gameplayCategoryRepository.findAll()).thenReturn(mockEntities);

        //mock dtoes to return from conversion in cacheAll
        GameplayCategoryDto mockDto = new GameplayCategoryDto();
            mockDto.setGameplayCategoryId(1);
            mockDto.setGameplayCategoryName("Test Category1");
        GameplayCategoryDto mockDto2 = new GameplayCategoryDto();
            mockDto2.setGameplayCategoryId(2);
            mockDto2.setGameplayCategoryName("Test Category2");
        when(gameplayCategoryConversionService.convertToDto(mockGameplayCategory)).thenReturn(mockDto);
        when(gameplayCategoryConversionService.convertToDto(mockGameplayCategory2)).thenReturn(mockDto2);

        //when
        List<GameplayCategoryDto> result = categoryCachingService.cacheAllCategories();

        //then
        assertAll("cacheAllCategories mock entities assertion set:",
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "Successful " +
                        "cacheAllCategories should not produce any error logs."),
                () -> assertNotNull(result, "cacheAllCategories should not return null"),
                () -> assertEquals(2, result.size(), "cacheAllCategories should return 2 mock DTOs"),
                () -> assertEquals(1, result.getFirst().getGameplayCategoryId(),
                        "cacheAllCategories produced dto with incorrect Id"),
                () -> assertEquals("Test Category1", result.getFirst().getGameplayCategoryName(),
                        "cacheAllCategories produced dto with incorrect Name"),
                () -> assertEquals(2, result.get(1).getGameplayCategoryId(),
                        "cacheAllCategories produced dto with incorrect Id"),
                () -> assertEquals("Test Category2", result.get(1).getGameplayCategoryName(),
                        "cacheAllCategories produced dto with incorrect Name"),
                () -> verify(gameplayCategoryRepository, times(1)).findAll());
    }

    @Test
    void testCacheAllCategories_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(CategoryCachingServiceImpl.class);
        //given category repository is empty

        //when
        //then
        assertAll("cacheAllCategories = empty assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () -> categoryCachingService.cacheAllCategories()),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Unable to access gameplay category data for caching."))),
                () -> verify(gameplayCategoryRepository, times(1)).findAll());
    }
}