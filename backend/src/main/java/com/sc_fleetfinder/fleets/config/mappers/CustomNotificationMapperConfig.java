package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.NotificationPrefsAndPushSubs.CreateOrEditCustomNotificationDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameEnvironment;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.NotificationPrefsAndPushSubs.GetCustomNotificationResponseDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameExperience;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GroupStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.Legality;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetarySystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PvpStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import com.sc_fleetfinder.fleets.entities.UserCustomNotification;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sc_fleetfinder.fleets.services.MapperLookupService;

@Configuration
public class CustomNotificationMapperConfig {

    private final MapperLookupService mls;

    public CustomNotificationMapperConfig(MapperLookupService mls) {
        this.mls = mls;
    }

    @Bean("customNotificationMapper")
    public ModelMapper customNotesMapper() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(CreateOrEditCustomNotificationDto.class, UserCustomNotification.class)
                .addMappings(mapper -> {

                    mapper.skip(UserCustomNotification::setCustomNoteId);
                    mapper.skip(UserCustomNotification::setUser);
                    mapper.skip(UserCustomNotification::setEnabled);
                    mapper.skip(UserCustomNotification::setCreatedAt);

                    mapper.map(CreateOrEditCustomNotificationDto::getTagLabel, UserCustomNotification::setTagLabel);

                    mapper.using((MappingContext<Integer, ServerRegion> ctx) -> mls.findServerRegionById(ctx.getSource()))
                            .map(CreateOrEditCustomNotificationDto::getServerId, UserCustomNotification::setServer);

                    mapper.using((MappingContext<Integer, GameEnvironment> ctx) -> mls.findEnvironmentById(ctx.getSource()))
                            .map(CreateOrEditCustomNotificationDto::getEnvironmentId, UserCustomNotification::setEnvironment);

                    mapper.using((MappingContext<Integer, GameExperience> ctx) -> mls.findExperienceById(ctx.getSource()))
                            .map(CreateOrEditCustomNotificationDto::getExperienceId, UserCustomNotification::setExperience);

                    mapper.using((MappingContext<Integer, GameplayCategory> ctx) -> mls.findCategoryById(ctx.getSource()))
                            .map(CreateOrEditCustomNotificationDto::getCategoryId, UserCustomNotification::setCategory);

                    mapper.using((MappingContext<Integer, GameplaySubcategory> ctx) -> mls.findSubcategoryById(ctx.getSource()))
                            .map(CreateOrEditCustomNotificationDto::getSubcategoryId, UserCustomNotification::setSubcategory);

                    mapper.using((MappingContext<Integer, PlanetarySystem> ctx) -> mls.findPlanetarySystemById(ctx.getSource()))
                            .map(CreateOrEditCustomNotificationDto::getSystemId, UserCustomNotification::setSystem);

                    mapper.map(CreateOrEditCustomNotificationDto::getLanguageCode, UserCustomNotification::setLanguageCode);

                    mapper.using((MappingContext<Integer, PvpStatus> ctx) -> mls.findPvpStatusById(ctx.getSource()))
                            .map(CreateOrEditCustomNotificationDto::getPvpStatusId, UserCustomNotification::setPvpStatus);

                    mapper.using((MappingContext<Integer, Legality> ctx) -> mls.findLegalityById(ctx.getSource()))
                            .map(CreateOrEditCustomNotificationDto::getLegalityId, UserCustomNotification::setLegality);

                    mapper.using((MappingContext<Integer, GroupStatus> ctx) -> mls.findGroupStatusById(ctx.getSource()))
                            .map(CreateOrEditCustomNotificationDto::getGroupStatusId, UserCustomNotification::setGroupStatus);

                    mapper.map(CreateOrEditCustomNotificationDto::getKeywords, UserCustomNotification::setKeywords);
                });



        modelMapper.createTypeMap(UserCustomNotification.class, GetCustomNotificationResponseDto.class).addMappings(mapper -> {

            mapper.map(UserCustomNotification::getCustomNoteId, GetCustomNotificationResponseDto::setCustomNoteId);

            mapper.map(UserCustomNotification::getEnabled, GetCustomNotificationResponseDto::setEnabled);

            mapper.map(UserCustomNotification::getTagLabel, GetCustomNotificationResponseDto::setTagLabel);

            mapper.using(ctx -> {
                ServerRegion server = (ServerRegion) ctx.getSource();
                return server != null ? server.getServerId() : null;
            }).map(UserCustomNotification::getServer, GetCustomNotificationResponseDto::setServerId);

            mapper.using(ctx -> {
                ServerRegion server = (ServerRegion) ctx.getSource();
                return server != null ? server.getServerName() : null;
            }).map(UserCustomNotification::getServer, GetCustomNotificationResponseDto::setServer);

            mapper.using(ctx -> {
                GameEnvironment environment = (GameEnvironment) ctx.getSource();
                return environment != null ? environment.getEnvironmentId() : null;
            }).map(UserCustomNotification::getEnvironment, GetCustomNotificationResponseDto::setEnvironmentId);

            mapper.using(ctx -> {
                GameEnvironment environment = (GameEnvironment) ctx.getSource();
                return environment != null ? environment.getEnvironmentType() : null;
            }).map(UserCustomNotification::getEnvironment, GetCustomNotificationResponseDto::setEnvironment);

            mapper.using(ctx -> {
                GameExperience experience = (GameExperience) ctx.getSource();
                return experience != null ? experience.getExperienceId() : null;
            }).map(UserCustomNotification::getExperience, GetCustomNotificationResponseDto::setExperienceId);

            mapper.using(ctx -> {
                GameExperience experience = (GameExperience) ctx.getSource();
                return experience != null ? experience.getExperienceType() : null;
            }).map(UserCustomNotification::getExperience, GetCustomNotificationResponseDto::setExperience);

            mapper.using(ctx -> {
                GameplayCategory cat = (GameplayCategory) ctx.getSource();
                return cat != null ? cat.getCategoryId() : null;
            }).map(UserCustomNotification::getCategory, GetCustomNotificationResponseDto::setCategoryId);

            mapper.using(ctx -> {
                GameplayCategory cat = (GameplayCategory) ctx.getSource();
                return cat != null ? cat.getCategoryName() : null;
            }).map(UserCustomNotification::getCategory, GetCustomNotificationResponseDto::setCategory);

            mapper.using(ctx -> {
                GameplaySubcategory subcat = (GameplaySubcategory) ctx.getSource();
                return subcat != null ? subcat.getSubcategoryId() : null;
            }).map(UserCustomNotification::getSubcategory, GetCustomNotificationResponseDto::setSubcategoryId);

            mapper.using(ctx -> {
                GameplaySubcategory subcat = (GameplaySubcategory) ctx.getSource();
                return subcat != null ? subcat.getSubcategoryName() : null;
            }).map(UserCustomNotification::getSubcategory, GetCustomNotificationResponseDto::setSubcategory);

            mapper.using(ctx -> {
                PlanetarySystem system = (PlanetarySystem) ctx.getSource();
                return system != null ? system.getSystemId() : null;
            }).map(UserCustomNotification::getSystem, GetCustomNotificationResponseDto::setSystemId);

            mapper.using(ctx -> {
                PlanetarySystem system = (PlanetarySystem) ctx.getSource();
                return system != null ? system.getSystemName() : null;
            }).map(UserCustomNotification::getSystem, GetCustomNotificationResponseDto::setSystem);

            mapper.map(UserCustomNotification::getLanguageCode, GetCustomNotificationResponseDto::setLanguageCode);

            mapper.using(ctx -> {
                PvpStatus pvp = (PvpStatus) ctx.getSource();
                return pvp != null ? pvp.getPvpStatusId() : null;
            }).map(UserCustomNotification::getPvpStatus, GetCustomNotificationResponseDto::setPvpStatusId);

            mapper.using(ctx -> {
                PvpStatus pvp = (PvpStatus) ctx.getSource();
                return pvp != null ? pvp.getPvpStatus() : null;
            }).map(UserCustomNotification::getPvpStatus, GetCustomNotificationResponseDto::setPvpStatus);

            mapper.using(ctx -> {
                Legality legality = (Legality) ctx.getSource();
                return legality != null ? legality.getLegalityId() : null;
            }).map(UserCustomNotification::getLegality, GetCustomNotificationResponseDto::setLegalityId);

            mapper.using(ctx -> {
                Legality legality = (Legality) ctx.getSource();
                return legality != null ? legality.getLegalityStatus() : null;
            }).map(UserCustomNotification::getLegality, GetCustomNotificationResponseDto::setLegality);

            mapper.using(ctx -> {
                GroupStatus group = (GroupStatus) ctx.getSource();
                return group != null ? group.getGroupStatusId() : null;
            }).map(UserCustomNotification::getGroupStatus, GetCustomNotificationResponseDto::setGroupStatusId);

            mapper.using(ctx -> {
                GroupStatus group = (GroupStatus) ctx.getSource();
                return group != null ? group.getGroupStatus() : null;
            }).map(UserCustomNotification::getGroupStatus, GetCustomNotificationResponseDto::setGroupStatus);

            mapper.map(UserCustomNotification::getKeywords, GetCustomNotificationResponseDto::setKeywords);

            mapper.map(UserCustomNotification::getCreatedAt, GetCustomNotificationResponseDto::setCreatedAt);
        });

        return modelMapper;
    }

}
