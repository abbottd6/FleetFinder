package com.sc_fleetfinder.fleets.config.mappers;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.DAO.PlanetMoonSystemRepository;
import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.DAO.PlayStyleRepository;
import com.sc_fleetfinder.fleets.DAO.PvpStatusRepository;
import com.sc_fleetfinder.fleets.DAO.ServerRegionRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateGroupListingMapperConfig {

    private final ModelMapper modelMapper;
    private final EnvironmentRepository environmentRepository;
    private final ExperienceRepository experienceRepository;
    private final GameplayCategoryRepository gameplayCategoryRepository;
    private final GameplaySubcategoryRepository gameplaySubcategoryRepository;
    private final GroupStatusRepository groupStatusRepository;
    private final LegalityRepository legalityRepository;
    private final PlanetarySystemRepository planetarySystemRepository;
    private final PlanetMoonSystemRepository planetMoonSystemRepository;
    private final PlayStyleRepository playStyleRepository;
    private final PvpStatusRepository pvpStatusRepository;
    private final ServerRegionRepository serverRegionRepository;
    private final UserRepository userRepository;

    public CreateGroupListingMapperConfig(ModelMapper modelMapper, EnvironmentRepository environmentRepository,
                                          ExperienceRepository experienceRepository, GameplayCategoryRepository gameplayCategoryRepository,
                                          GameplaySubcategoryRepository gameplaySubcategoryRepository,
                                          GroupStatusRepository groupStatusRepository, LegalityRepository legalityRepository,
                                          PlanetarySystemRepository planetarySystemRepository,
                                          PlanetMoonSystemRepository planetMoonSystemRepository, PlayStyleRepository playStyleRepository,
                                          PvpStatusRepository pvpStatusRepository, ServerRegionRepository serverRegionRepository,
                                          UserRepository userRepository) {

        this.modelMapper = modelMapper;
        this.environmentRepository = environmentRepository;
        this.experienceRepository = experienceRepository;
        this.gameplayCategoryRepository = gameplayCategoryRepository;
        this.gameplaySubcategoryRepository = gameplaySubcategoryRepository;
        this.groupStatusRepository = groupStatusRepository;
        this.legalityRepository = legalityRepository;
        this.planetarySystemRepository = planetarySystemRepository;
        this.planetMoonSystemRepository = planetMoonSystemRepository;
        this.playStyleRepository = playStyleRepository;
        this.pvpStatusRepository = pvpStatusRepository;
        this.serverRegionRepository = serverRegionRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void configureMapping() {
        modelMapper.createTypeMap(CreateGroupListingDto.class, GroupListing.class)
                .addMappings(mapper -> {
                    mapper.skip(GroupListing::setGroupId);
                    mapper.skip(GroupListing::setCreationTimestamp);

                });
    }
}
