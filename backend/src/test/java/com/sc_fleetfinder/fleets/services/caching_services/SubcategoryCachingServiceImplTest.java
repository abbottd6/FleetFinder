package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.GameEnvironmentServiceImpl;
import com.sc_fleetfinder.fleets.services.GameplaySubcategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
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
    private GameplaySubcategoryServiceImpl subcategoryService;

    @InjectMocks
    private SubcategoryCachingServiceImpl subcategoryCachingService;

    @Test
    void testCacheAllSubcategories_Found() {
        //given
        GameplaySubcategory mockGameplaySubcategory1 = new GameplaySubcategory();
        GameplaySubcategory mockGameplaySubcategory2 = new GameplaySubcategory();
        List<GameplaySubcategory> mockEntities = List.of(mockGameplaySubcategory1, mockGameplaySubcategory2);
        when(subcategoryRepository.findAll()).thenReturn(mockEntities);

        GameplaySubcategoryDto mockDto1 = new GameplaySubcategoryDto();
        GameplaySubcategoryDto mockDto2 = new GameplaySubcategoryDto();
        when(subcategoryService.convertToDto(mockGameplaySubcategory1)).thenReturn(mockDto1);
        when(subcategoryService.convertToDto(mockGameplaySubcategory2)).thenReturn(mockDto2);

        //when
        List<GameplaySubcategoryDto> result = subcategoryCachingService.cacheAllSubcategories();

        //then
        assertAll("get all subcategories mock entities assertions set:",
                () -> assertNotNull(result, "get all subcategories should not return null"),
                () -> assertEquals(2, result.size(), "get all subcategories should contain 2 elements."),
                () -> verify(subcategoryRepository, times(1)).findAll());
    }

    @Test
    void testCacheAllSubcategories_NotFound() {
        //given subcategories repo is empty

        //when
        //then
        assertAll("get all subcategories = empty assertions set:",
                () -> assertThrows(ResourceNotFoundException.class, () -> subcategoryCachingService.cacheAllSubcategories()),
                () -> verify(subcategoryRepository, times(1)).findAll());
    }
}