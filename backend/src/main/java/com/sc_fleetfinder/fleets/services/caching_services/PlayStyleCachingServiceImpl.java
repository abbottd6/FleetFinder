package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.PlayStyleRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlayStyleDto;
import com.sc_fleetfinder.fleets.entities.PlayStyle;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.PlayStyleConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayStyleCachingServiceImpl implements PlayStyleCachingService {

    private static final Logger log = LoggerFactory.getLogger(PlayStyleCachingServiceImpl.class);
    private final PlayStyleRepository playStyleRepository;
    private final PlayStyleConversionService playStyleConversionService;

    @Autowired
    public PlayStyleCachingServiceImpl(PlayStyleRepository playStyleRepository,
                                       PlayStyleConversionService playStyleConversionService) {
        this.playStyleRepository = playStyleRepository;
        this.playStyleConversionService = playStyleConversionService;
    }

    @Override
    @Cacheable(value = "playStylesCache", key = "'allPlayStyles'")
    public List<PlayStyleDto> cacheAllPlayStyles() {
        List<PlayStyle> playStyles = playStyleRepository.findAll();

        if(playStyles.isEmpty()) {
            log.error("Unable to access play style data for caching.");
            throw new ResourceNotFoundException("Unable to access play style data.");
        }

        return playStyles.stream()
                .map(playStyleConversionService::convertToDto)
                .collect(Collectors.toList());
    }
}
