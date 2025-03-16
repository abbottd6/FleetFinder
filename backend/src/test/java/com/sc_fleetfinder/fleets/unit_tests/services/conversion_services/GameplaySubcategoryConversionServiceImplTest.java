package com.sc_fleetfinder.fleets.unit_tests.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
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
        LogCaptor logCaptor = LogCaptor.forClass(GameplaySubcategoryConversionServiceImpl.class);
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
                () -> assertDoesNotThrow(() ->
                        gameplaySubcategoryConversionService.convertToDto(mockGameplaySubcategory1)),
                () -> assertDoesNotThrow(() ->
                        gameplaySubcategoryConversionService.convertToDto(mockGameplaySubcategory2)),
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
        LogCaptor logCaptor = LogCaptor.forClass(GameplaySubcategoryConversionServiceImpl.class);
        //given: a parent category entity for initializing field
        GameplayCategory mockGameplayCategory = new GameplayCategory();
        mockGameplayCategory.setCategoryId(1);
        mockGameplayCategory.setCategoryName("Test Category");

        //and a subcategory entity with a null id
        GameplaySubcategory mockEntity = new GameplaySubcategory();
        mockEntity.setSubcategoryId(null);
        mockEntity.setSubcategoryName("Test Subcategory1");
        mockEntity.setGameplayCategory(mockGameplayCategory);

        //when
        //then
        assertAll("Subcategory convertToDto_FailIdNull assertion set: ",

                () -> assertThrows(IllegalArgumentException.class, () ->
                                gameplaySubcategoryConversionService.convertToDto(mockEntity),
                        "convert subcategory entity to Dto should throw exception when Id is null"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Subcategory convertToDto encountered an entity with an id " +
                                "that is null or 0."))));
    }

    @Test
    void testConvertToDto_FailIdZero() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplaySubcategoryConversionServiceImpl.class);
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
        assertAll("Subcategory convertToDto_FailIdZero assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () ->
                                gameplaySubcategoryConversionService.convertToDto(mockEntity),
                        "convert subcategory entity to Dto should throw exception when Id is zero"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Subcategory convertToDto encountered an entity with an id " +
                                "that is null or 0."))));
    }

    @Test
    void testConvertToDto_FailNameIsNull() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplaySubcategoryConversionServiceImpl.class);
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
        assertAll("Subcategory convertToDto_FailNameIsNull assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () ->
                                gameplaySubcategoryConversionService.convertToDto(mockEntity),
                        "convert subcategory entity to Dto should throw exception when Name is null"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Subcategory convertToDto encountered an entity with an name " +
                                "that is null or empty."))));
    }

    @Test
    void testConvertToDto_FailNameIsEmptyString() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplaySubcategoryConversionServiceImpl.class);
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
        assertAll("Subcategory convertToDto_FailNameIsEmptyString assertion set: ",
                () -> assertThrows(IllegalArgumentException.class, () ->
                                gameplaySubcategoryConversionService.convertToDto(mockEntity),
                        "convert subcategory entity to Dto should throw exception when Name is empty"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Subcategory convertToDto encountered an entity with an " +
                                "name that is null or empty."))));
    }

    @Test
    void testConvertToEntity_Found() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplaySubcategoryConversionServiceImpl.class);
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
                () -> assertDoesNotThrow(() ->
                        gameplaySubcategoryConversionService.convertToEntity(mockGameplaySubcategoryDto)),
                () -> assertEquals(0, logCaptor.getErrorLogs().size(), "Successful Subcategory " +
                        "convertToEntity should not produce any error logs."),
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
        LogCaptor logCaptor = LogCaptor.forClass(GameplaySubcategoryConversionServiceImpl.class);
        //given
        GameplaySubcategoryDto mockGameplaySubcategoryDto = new GameplaySubcategoryDto();
        mockGameplaySubcategoryDto.setSubcategoryId(1);
        mockGameplaySubcategoryDto.setSubcategoryName("Not Subcategory1");
        when(gameplaySubcategoryRepository.findById(1)).thenReturn(Optional.empty());

        //when backend entity does not exist with dto ID

        //then
        assertAll("Subcategory convertToEntity_NotFound assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                                gameplaySubcategoryConversionService.convertToEntity(mockGameplaySubcategoryDto),
                        "convertToEntity with id not found should throw exception"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Subcategory convertToEntity could not find entity " +
                                "with Id: 1"))));
    }

    @Test
    void testConvertToEntity_NameMismatch() {
        LogCaptor logCaptor = LogCaptor.forClass(GameplaySubcategoryConversionServiceImpl.class);
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
        assertAll("Subcategory convertToEntity_NameMismatch assertion set: ",
                () -> assertThrows(ResourceNotFoundException.class, () ->
                                gameplaySubcategoryConversionService.convertToEntity(mockGameplaySubcategoryDto),
                        "convertToEntity with id/name mismatch should throw exception"),
                () -> assertTrue(logCaptor.getErrorLogs().stream()
                        .anyMatch(log -> log.contains("Subcategory convertToEntity encountered a id/name " +
                                "mismatch for Id: 1, and subcategory: Wrong Subcategory1"))));
    }
}