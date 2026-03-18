package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetPushSubDto;
import com.sc_fleetfinder.fleets.entities.PushSubscription;
import com.sc_fleetfinder.fleets.entities.Users;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PushSubscriptionMapperConfig {

    @Bean("pushSubscriptionMapper")
    public ModelMapper pushSubscriptionMapper() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(PushSubscription.class, GetPushSubDto.class)
                .addMappings(mapper -> {

                    mapper.using(ctx -> {
                        Users user = (Users) ctx.getSource();
                        return user != null ? user.getUserId() : null;
                    }).map(PushSubscription::getUser, GetPushSubDto::setUserId);
                });
        return modelMapper;
    }
}
