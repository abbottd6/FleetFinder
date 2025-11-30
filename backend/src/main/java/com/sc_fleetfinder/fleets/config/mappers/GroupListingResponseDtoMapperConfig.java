package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameExperience;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GroupStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.Legality;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetarySystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlayStyle;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PvpStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import com.sc_fleetfinder.fleets.entities.Users;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Configuration
public class GroupListingResponseDtoMapperConfig {

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm")
            .withZone(ZoneOffset.UTC);

    public GroupListingResponseDtoMapperConfig() {}

    @Bean
    public ModelMapper groupListingResponseDtoMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.addConverter(new AbstractConverter<Instant, String>() {
            @Override
            protected String convert(Instant source) {
                return source != null ? UTC_FORMATTER.format(source) : null;
            }
        });

        modelMapper.createTypeMap(GroupListing.class, GroupListingResponseDto.class)
                .addMappings(mapper -> {

                    mapper.map(GroupListing::getGroupId, GroupListingResponseDto::setGroupId);

                    mapper.using(ctx -> {
                        Users users = (Users) ctx.getSource();
                        return users != null ? users.getUsername() : null;
                    }).map(GroupListing::getUsers, GroupListingResponseDto::setUserName);

                    mapper.using(ctx -> {
                        ServerRegion server = (ServerRegion) ctx.getSource();
                        return server != null ? server.getServerName() : null;
                    }).map(GroupListing::getServer, GroupListingResponseDto::setServer);

                    mapper.using(ctx -> {
                        GameEnvironment environment = (GameEnvironment) ctx.getSource();
                        return environment != null ? environment.getEnvironmentType() : null;
                    }).map(GroupListing::getEnvironment, GroupListingResponseDto::setEnvironment);

                    mapper.using(ctx -> {
                        GameExperience experience = (GameExperience) ctx.getSource();
                        return experience != null ? experience.getExperienceType() : null;
                    }).map(GroupListing::getExperience, GroupListingResponseDto::setExperience);

                    mapper.map(GroupListing::getListingTitle, GroupListingResponseDto::setListingTitle);

                    //checking nullable field for null to convert to an empty string if it is null
                    mapper.using(ctx -> {
                        PlayStyle playStyle = (PlayStyle) ctx.getSource();
                        if (playStyle == null) {
                            return "";
                        }
                        else {
                            return playStyle.getPlayStyle();
                        }
                    }).map(GroupListing::getPlayStyle, GroupListingResponseDto::setPlayStyle);

                    mapper.using(ctx -> {
                        Legality legality = (Legality) ctx.getSource();
                        return legality != null ? legality.getLegalityStatus() : null;
                    }).map(GroupListing::getLegality, GroupListingResponseDto::setLegality);

                    mapper.using(ctx -> {
                        GroupStatus groupStatus = (GroupStatus) ctx.getSource();
                        return groupStatus != null ? groupStatus.getGroupStatus() : null;
                    }).map(GroupListing::getGroupStatus, GroupListingResponseDto::setGroupStatus);

                    mapper.map(GroupListing::getEventSchedule, GroupListingResponseDto::setEventSchedule);

                    mapper.using(ctx -> {
                        GameplayCategory category = (GameplayCategory) ctx.getSource();
                        return category != null ? category.getCategoryName() : null;
                    }).map(GroupListing::getCategory, GroupListingResponseDto::setCategory);

                    mapper.using(ctx -> {
                        GameplaySubcategory subcategory = (GameplaySubcategory) ctx.getSource();
                        if(subcategory == null) {
                            return "";
                        }
                        else {
                            return subcategory.getSubcategoryName();
                        }
                    }).map(GroupListing::getSubcategory, GroupListingResponseDto::setSubcategory);

                    mapper.using(ctx -> {
                        PvpStatus pvpStatus = (PvpStatus) ctx.getSource();
                        return pvpStatus != null ? pvpStatus.getPvpStatus() : null;
                    }).map(GroupListing::getPvpStatus, GroupListingResponseDto::setPvpStatus);

                    mapper.using(ctx -> {
                        PlanetarySystem system = (PlanetarySystem) ctx.getSource();
                        return system != null ? system.getSystemName() : null;
                    }).map(GroupListing::getSystem, GroupListingResponseDto::setSystem);

                    mapper.using(ctx -> {
                        PlanetMoonSystem planet = (PlanetMoonSystem) ctx.getSource();
                        if (planet == null) {
                            return "";
                        }
                        else {
                            return planet.getPlanetName();
                        }
                    }).map(GroupListing::getPlanetMoonSystem, GroupListingResponseDto::setPlanetMoonSystem);

                    mapper.map(GroupListing::getListingDescription, GroupListingResponseDto::setListingDescription);

                    mapper.map(GroupListing::getDesiredPartySize, GroupListingResponseDto::setDesiredPartySize);

                    mapper.map(GroupListing::getCurrentPartySize, GroupListingResponseDto::setCurrentPartySize);

                    mapper.using(ctx -> {
                        String availableRoles = (String) ctx.getSource();
                        return Objects.requireNonNullElse(availableRoles, "");
                    }).map(GroupListing::getAvailableRoles, GroupListingResponseDto::setAvailableRoles);

                    mapper.map(GroupListing::getCommsOption, GroupListingResponseDto::setCommsOption);

                    mapper.using(ctx -> {
                        String commsService = (String) ctx.getSource();
                        return Objects.requireNonNullElse(commsService, "");
                    }).map(GroupListing::getCommsService, GroupListingResponseDto::setCommsService);

                    mapper.map(GroupListing::getCreationTimestamp, GroupListingResponseDto::setCreationTimestamp);

                    mapper.map(GroupListing::getLastUpdated, GroupListingResponseDto::setLastUpdated);
                });
        return modelMapper;
    }
}
