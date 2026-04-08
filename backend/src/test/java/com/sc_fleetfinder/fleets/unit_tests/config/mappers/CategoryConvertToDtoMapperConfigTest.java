package com.sc_fleetfinder.fleets.unit_tests.config.mappers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.config.mappers.ModelMapperConfig;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;
import com.sc_fleetfinder.fleets.services.MapperLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.config.Configuration;

import static org.junit.jupiter.api.Assertions.*;

class CategoryConvertToDtoMapperConfigTest {

    private ModelMapper modelMapper = new ModelMapper();
    private MapperLookupService mls;

    @BeforeEach
    void setUp() {
        ModelMapperConfig config = new ModelMapperConfig(mls);
        modelMapper = config.fleetFinderModelMapper();
    }

    @Test
    void ConfigurationTestCategoryConvertToDtoMapper() {
        assertNotNull(modelMapper);

        Configuration configuration = modelMapper.getConfiguration();

        System.out.println(configuration.getMatchingStrategy());
        assertEquals(MatchingStrategies.STRICT, configuration.getMatchingStrategy());
    }

    @Test
    void testCategoryEntityToDtoMapping_Success() {
        //given
        GameplayCategory mockCategory = new GameplayCategory();
        mockCategory.setCategoryId(1);
        mockCategory.setCategoryName("Mock Category");

        //when
        GameplayCategoryDto resultDto = modelMapper.map(mockCategory, GameplayCategoryDto.class);

        //then
        assertAll(
                () -> assertEquals(mockCategory.getCategoryId(), resultDto.getGameplayCategoryId()),
                () -> assertEquals(mockCategory.getCategoryName(), resultDto.getGameplayCategoryName())
        );
    }
}