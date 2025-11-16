package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//this is not used currently
@Configuration
public class SubcategoryConvertEntityToDtoMapperConfig {

    @Bean
    public ModelMapper SubcategoryConvertEntityToDtoMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(GameplaySubcategory.class, GameplaySubcategoryDto.class)
                .addMappings(mapper -> {

                    //skipping implicit mappings with matching field names
                    mapper.skip(GameplaySubcategoryDto::setSubcategoryId);
                    mapper.skip(GameplaySubcategoryDto::setSubcategoryName);

                    mapper.map(GameplaySubcategory::getGameplayCategory, GameplaySubcategoryDto::setGameplayCategoryId);

                    //subcategory entity gameplayCategory to dto gameplayCategoryName
                    mapper.map(GameplaySubcategory::getGameplayCategory, GameplaySubcategoryDto::setGameplayCategoryName);
                });

        return modelMapper;
    }
}
