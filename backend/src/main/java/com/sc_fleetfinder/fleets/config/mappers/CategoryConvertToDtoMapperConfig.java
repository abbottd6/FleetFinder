package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryConvertToDtoMapperConfig {

    //Category entity to CategoryDto, for fetching responses
    @Bean
    public ModelMapper CategoryConvertToDtoMapper() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(GameplayCategory.class, GameplayCategoryDto.class)
                .addMappings(mapper -> {

                    //entity categoryId to dto gameplayCategoryId
                    mapper.map(GameplayCategory::getCategoryId, GameplayCategoryDto::setGameplayCategoryId);

                    //entity categoryName to dto gameplayCategoryName
                    mapper.map(GameplayCategory::getCategoryName, GameplayCategoryDto::setGameplayCategoryName);
                });

        return modelMapper;
    }
}
