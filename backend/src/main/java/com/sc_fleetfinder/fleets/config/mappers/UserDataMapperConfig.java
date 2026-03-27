package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PrivateUserResponseDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import com.sc_fleetfinder.fleets.entities.Users;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserDataMapperConfig {

    public UserDataMapperConfig() {}

    @Bean
    public ModelMapper privateDtoMapper() {
        ModelMapper privateUserDtoMapper = new ModelMapper();

        privateUserDtoMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        privateUserDtoMapper.createTypeMap(Users.class, PrivateUserResponseDto.class)
                .addMappings(mapper -> {
                    mapper.using(ctx -> {
                        ServerRegion serverRegion = (ServerRegion) ctx.getSource();
                        return serverRegion != null ? serverRegion.getServerName() : null;
                    }).map(Users::getServerId, PrivateUserResponseDto::setServer);
                });
        return privateUserDtoMapper;
    }
}
