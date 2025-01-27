package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.services.MapperLookupService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

                    //subcategory entity gameplayCategory to dto gameplayCategoryName
                    mapper.map(GameplaySubcategory::getGameplayCategory, GameplaySubcategoryDto::setGameplayCategoryName);
                });

        return modelMapper;
    }
}
