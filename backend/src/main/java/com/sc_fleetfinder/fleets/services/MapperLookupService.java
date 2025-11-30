package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.ExperienceRepository;
import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.LegalityRepository;
import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.PlanetMoonSystemRepository;
import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.PlayStyleRepository;
import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.PvpStatusRepository;
import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.ServerRegionRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameExperience;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GroupStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.Legality;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetarySystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlayStyle;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PvpStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MapperLookupService {

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


    public MapperLookupService(EnvironmentRepository environmentRepository,
                               ExperienceRepository experienceRepository, GameplayCategoryRepository gameplayCategoryRepository,
                               GameplaySubcategoryRepository gameplaySubcategoryRepository,
                               GroupStatusRepository groupStatusRepository, LegalityRepository legalityRepository,
                               PlanetarySystemRepository planetarySystemRepository,
                               PlanetMoonSystemRepository planetMoonSystemRepository, PlayStyleRepository playStyleRepository,
                               PvpStatusRepository pvpStatusRepository, ServerRegionRepository serverRegionRepository,
                               UserRepository userRepository) {
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

    //Make these cacheable??

    public Users findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }

    public ServerRegion findServerRegionById(Integer id) {
        return serverRegionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServerRegion", id));
    }

    public GameEnvironment findEnvironmentById(Integer id) {
        return environmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GameEnvironment", id));
    }

    public GameExperience findExperienceById(Integer id) {
        return experienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GameExperience", id));
    }

    public PlayStyle findPlayStyleById(Integer id) {
        return playStyleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlayStyle", id));
    }

    public Legality findLegalityById(Integer id) {
        return legalityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legality", id));
    }

    public GroupStatus findGroupStatusById(Integer id) {
        return groupStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GroupStatus", id));
    }

    public GameplayCategory findCategoryById(Integer id) {
        return gameplayCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GameplayCategory", id));
    }

    public GameplayCategory findCategoryByName(String categoryName) {
        return gameplayCategoryRepository.findByCategoryName(categoryName)
                .orElseThrow(org.springframework.data.rest.webmvc.ResourceNotFoundException::new);
    }

    public GameplaySubcategory findSubcategoryById(Integer id) {
        return gameplaySubcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GameplaySubcategory", id));
    }

    public PvpStatus findPvpStatusById(Integer id) {
        return pvpStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PvpStatus", id));
    }

    public PlanetarySystem findPlanetarySystemById(Integer id) {
        return planetarySystemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlanetarySystem", id));
    }

    public PlanetMoonSystem findPlanetMoonSystemById(Integer id) {
        return planetMoonSystemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlanetMoonSystem", id));
    }
}
