package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.GameplaySubcategoryConversionServiceImpl;
import nl.altindag.log.LogCaptor;
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
class SubcategoryCachingServiceImplTest {

    @Mock
    private GameplaySubcategoryRepository subcategoryRepository;

    @Mock
    private GameplaySubcategoryConversionServiceImpl gameplaySubcategoryConversionService;

    @InjectMocks
    private SubcategoryCachingServiceImpl subcategoryCachingService;

    @Test
    void testCacheAllSubcategories_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(SubcategoryCachingServiceImpl.class);
        //given
        GameplaySubcategory mockGameplaySubcategory1 = new GameplaySubcategory();
        GameplaySubcategory mockGameplaySubcategory2 = new GameplaySubcategory();
        List<GameplaySubcategory> mockEntities = List.of(mockGameplaySubcategory1, mockGameplaySubcategory2);
        when(subcategoryRepository.findAll()).thenReturn(mockEntities);

        GameplaySubcategoryDto mockDto1 = new GameplaySubcategoryDto();
        GameplaySubcategoryDto mockDto2 = new GameplaySubcategoryDto();
        when(gameplaySubcategoryConversionService.convertToDto(mockGameplaySubcategory1)).thenReturn(mockDto1);
        when(gameplaySubcategoryConversionService.convertToDto(mockGameplaySubcategory2)).thenReturn(mockDto2);

        //when
        List<GameplaySubcategoryDto> result = subcategoryCachingService.cacheAllSubcategories();

        //then
        assertAll("get all subcategories mock entities assertions set:",
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "Successful " +
                        "cacheAllSubcategories should not produce any error logs."),
                () -> assertNotNull(result, "get all subcategories should not return null"),
                () -> assertEquals(2, result.size(), "get all subcategories should contain 2 elements."),
                () -> verify(subcategoryRepository, times(1)).findAll());
    }

    @Test
    void testCacheAllSubcategories_NotFound() {
        LogCaptor logCaptor = LogCaptor.forClass(SubcategoryCachingServiceImpl.class);
        //given subcategories repo is empty

        //when
        //then
        assertAll("get all subcategories = empty assertions set:",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                        subcategoryCachingService.cacheAllSubcategories()),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Unable to access Subcategory data for caching."))),
                () -> verify(subcategoryRepository, times(1)).findAll());
    }
}