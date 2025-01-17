package com.sc_fleetfinder.fleets.config.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}


/*
ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.typeMap(CreateGroupListingDto.class, GroupListing.class)
                .addMappings(mapper -> mapper.skip(GroupListing::setCreationTimestamp));

        return modelMapper;
    }
 */