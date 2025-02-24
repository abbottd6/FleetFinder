package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
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
class GameplaySubcategoryConversionServiceImplTest {

    @InjectMocks
    private GameplaySubcategoryConversionServiceImpl gameplaySubcategoryConversionService;

    @Mock
    private GameplaySubcategoryRepository gameplaySubcategoryRepository;

    @Test
    void testConvertToDto_Success() {
        //given
        //parent gameplay category
        GameplayCategory mockGameplayCategory = new GameplayCategory();
        mockGameplayCategory.setCategoryId(1);
        mockGameplayCategory.setCategoryName("Test Category");

        //gameplay subcategories
        GameplaySubcategory mockGameplaySubcategory1 = new GameplaySubcategory();
        mockGameplaySubcategory1.setSubcategoryId(1);
        mockGameplaySubcategory1.setSubcategoryName("Test Subcategory1");
        mockGameplaySubcategory1.setGameplayCategory(mockGameplayCategory);
        GameplaySubcategory mockGameplaySubcategory2 = new GameplaySubcategory();
        mockGameplaySubcategory2.setSubcategoryId(2);
        mockGameplaySubcategory2.setSubcategoryName("Test Subcategory2");
        mockGameplaySubcategory2.setGameplayCategory(mockGameplayCategory);

        //adding subcategories to parent gameplay category set
        mockGameplayCategory.getGameplaySubcategories().addAll(List.of(mockGameplaySubcategory1, mockGameplaySubcategory2));


        //when
        List<GameplaySubcategoryDto> result = List.of(gameplaySubcategoryConversionService.convertToDto(mockGameplaySubcategory1),
                gameplaySubcategoryConversionService.convertToDto(mockGameplaySubcategory2));

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
    void testConvertToDto_FailIdNull() {
        //given
        GameplayCategory mockGameplayCategory = new GameplayCategory();
        mockGameplayCategory.setCategoryId(1);
        mockGameplayCategory.setCategoryName("Test Category");

        GameplaySubcategory mockEntity = new GameplaySubcategory();
        mockEntity.setSubcategoryId(null);
        mockEntity.setSubcategoryName("Test Subcategory1");
        mockEntity.setGameplayCategory(mockGameplayCategory);

        //when
        //then
        assertThrows(ResourceNotFoundException.class,
                () -> gameplaySubcategoryConversionService.convertToDto(mockEntity),
                "convert subcategory entity to Dto should throw exception when Id is null");
    }

    @Test
    void testConvertToDto_FailIdZero() {
        //given
        GameplayCategory mockGameplayCategory = new GameplayCategory();
        mockGameplayCategory.setCategoryId(1);
        mockGameplayCategory.setCategoryName("Test Category");

        GameplaySubcategory mockEntity = new GameplaySubcategory();
        mockEntity.setSubcategoryId(0);
        mockEntity.setSubcategoryName("Test Subcategory1");
        mockEntity.setGameplayCategory(mockGameplayCategory);

        //when
        //then
        assertThrows(ResourceNotFoundException.class,
                () -> gameplaySubcategoryConversionService.convertToDto(mockEntity),
                "convert subcategory entity to Dto should throw exception when Id is zero");
    }

    @Test
    void testConvertToDto_FailNameIsNull() {
        //given
        GameplayCategory mockGameplayCategory = new GameplayCategory();
        mockGameplayCategory.setCategoryId(1);
        mockGameplayCategory.setCategoryName("Test Category");

        GameplaySubcategory mockEntity = new GameplaySubcategory();
        mockEntity.setSubcategoryId(1);
        mockEntity.setSubcategoryName(null);
        mockEntity.setGameplayCategory(mockGameplayCategory);

        //when
        //then
        assertThrows(ResourceNotFoundException.class,
                () -> gameplaySubcategoryConversionService.convertToDto(mockEntity),
                "convert subcategory entity to Dto should throw exception when Name is null");
    }

    @Test
    void testConvertToDto_FailNameIsEmptyString() {
        //given
        GameplayCategory mockGameplayCategory = new GameplayCategory();
        mockGameplayCategory.setCategoryId(1);
        mockGameplayCategory.setCategoryName("Test Category");

        GameplaySubcategory mockEntity = new GameplaySubcategory();
        mockEntity.setSubcategoryId(1);
        mockEntity.setSubcategoryName("");
        mockEntity.setGameplayCategory(mockGameplayCategory);

        //when
        //then
        assertThrows(ResourceNotFoundException.class,
                () -> gameplaySubcategoryConversionService.convertToDto(mockEntity),
                "convert subcategory entity to Dto should throw exception when Name is empty");
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
        GameplaySubcategory mockEntity = gameplaySubcategoryConversionService.convertToEntity(mockGameplaySubcategoryDto);

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
                        gameplaySubcategoryConversionService.convertToEntity(mockGameplaySubcategoryDto),
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
                        gameplaySubcategoryConversionService.convertToEntity(mockGameplaySubcategoryDto),
                "convertToEntity with id/name mismatch should throw exception");
    }
}