package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DAO.GroupStatusRepository;
import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.DAO.PlanetMoonSystemRepository;
import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.DAO.PlayStyleRepository;
import com.sc_fleetfinder.fleets.DAO.PvpStatusRepository;
import com.sc_fleetfinder.fleets.DAO.ServerRegionRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.entities.GroupStatus;
import com.sc_fleetfinder.fleets.entities.Legality;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.entities.PlayStyle;
import com.sc_fleetfinder.fleets.entities.PvpStatus;
import com.sc_fleetfinder.fleets.entities.ServerRegion;
import com.sc_fleetfinder.fleets.entities.User;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
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

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
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
