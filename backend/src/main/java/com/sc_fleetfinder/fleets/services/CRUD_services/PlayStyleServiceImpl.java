package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlayStyleDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.PlayStyleCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayStyleServiceImpl implements PlayStyleService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final PlayStyleCachingService playStyleCachingService;

    public PlayStyleServiceImpl(PlayStyleCachingService playStyleCachingService) {
        this.playStyleCachingService = playStyleCachingService;
    }

    @Override
    public List<PlayStyleDto> getAllPlayStyles() {
        return playStyleCachingService.cacheAllPlayStyles();
    }

    @Override
    public PlayStyleDto getPlayStyleById(Integer id) {
        List<PlayStyleDto> cachedPlayStyles = playStyleCachingService.cacheAllPlayStyles();

        return cachedPlayStyles.stream()
                .filter(style -> style.getStyleId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Could not find play style with id {} in the cached play styles", id);
                    return new ResourceNotFoundException(id);
                });
    }
}
