package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.entities.Legality;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.LegalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LegalityCachingServiceImpl implements LegalityCachingService{

    private final LegalityRepository legalityRepository;
    private final LegalityService legalityService;

    @Autowired
    public LegalityCachingServiceImpl(LegalityRepository legalityRepository, LegalityService legalityService) {
        this.legalityRepository = legalityRepository;
        this.legalityService = legalityService;
    }

    @Override
    @Cacheable(value = "legalitiesCache", key = "'allLegalitiesCache'")
    public List<LegalityDto> cacheAllLegalities() {
        List<Legality> legalities = legalityRepository.findAll();

        if(legalities.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for legalities");
        }

        return legalities.stream()
                .map(legalityService::convertToDto)
                .collect(Collectors.toList());
    }
}
