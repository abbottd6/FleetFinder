package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.services.MapperLookupService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryConvertDtoToEntityMapperConfig {

    private final MapperLookupService mapperLookupService;

    public CategoryConvertDtoToEntityMapperConfig(MapperLookupService mapperLookupService) {
        this.mapperLookupService = mapperLookupService;
    }

    @Bean
    public ModelMapper CategoryConvertDtoToEntityMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(GameplayCategoryDto.class, GameplayCategory.class)
                .addMappings(mapper -> {

                    //skipping unused field gameplaySubcategories
                    mapper.skip(GameplayCategory::setGameplaySubcategories);

                    mapper.using((MappingContext<Integer, GameplayCategory> ctx) ->
                            mapperLookupService.findCategoryById(ctx.getSource()))
                            .map(GameplayCategoryDto::getGameplayCategoryId, GameplayCategory::setCategoryId);

                    mapper.using((MappingContext<String, GameplayCategory> ctx) ->
                            mapperLookupService.findCategoryByName(ctx.getSource()))
                            .map(GameplayCategoryDto::getGameplayCategoryName, GameplayCategory::setCategoryName);
                });

        return modelMapper;
    }
}
