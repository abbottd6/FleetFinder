package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.entities.Legality;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.LegalityConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LegalityCachingServiceImpl implements LegalityCachingService{

    private static final Logger log = LoggerFactory.getLogger(LegalityCachingServiceImpl.class);
    private final LegalityRepository legalityRepository;
    private final LegalityConversionService legalityConversionService;

    @Autowired
    public LegalityCachingServiceImpl(LegalityRepository legalityRepository,
                                      LegalityConversionService legalityConversionService) {
        this.legalityRepository = legalityRepository;
        this.legalityConversionService = legalityConversionService;
    }

    @Override
    @Cacheable(value = "legalitiesCache", key = "'allLegalitiesCache'")
    public List<LegalityDto> cacheAllLegalities() {
        List<Legality> legalities = legalityRepository.findAll();

        if(legalities.isEmpty()) {
            log.error("Unable to access legalities data for caching.");
            throw new ResourceNotFoundException("Unable to access data for legalities");
        }

        return legalities.stream()
                .map(legalityConversionService::convertToDto)
                .collect(Collectors.toList());
    }
}
