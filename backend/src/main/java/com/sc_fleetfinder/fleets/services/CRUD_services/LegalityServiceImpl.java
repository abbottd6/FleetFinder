package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.LegalityCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LegalityServiceImpl implements LegalityService {

    private static final Logger log = LoggerFactory.getLogger(LegalityServiceImpl.class);
    private final LegalityCachingService legalityCachingService;

    public LegalityServiceImpl(LegalityCachingService legalityCachingService) {
        this.legalityCachingService = legalityCachingService;
    }

    @Override
    public List<LegalityDto> getAllLegalities() {
        return legalityCachingService.cacheAllLegalities();
    }

    @Override
    public LegalityDto getLegalityById(Integer id) {
        List<LegalityDto> cachedLegalities = legalityCachingService.cacheAllLegalities();

        return cachedLegalities.stream()
                .filter(legality -> legality.getLegalityId().equals(id))
                .findFirst()
                .orElseGet(() -> {
                    log.error("Legality requested but not found for Id: {}", id);
                    throw new ResourceNotFoundException(id);
                });
    }


}
