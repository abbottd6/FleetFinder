package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.PvpStatusRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.PvpStatusDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PvpStatus;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.ListingReferenceDataConversionServices.PvpStatusConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PvpStatusCachingServiceImpl implements PvpStatusCachingService {

    private static final Logger log = LoggerFactory.getLogger(PvpStatusCachingServiceImpl.class);
    private final PvpStatusRepository pvpStatusRepository;
    private final PvpStatusConversionService pvpStatusConversionService;

    @Autowired
    public PvpStatusCachingServiceImpl(PvpStatusRepository pvpStatusRepository,
                                       PvpStatusConversionService pvpStatusConversionService) {
        this.pvpStatusRepository = pvpStatusRepository;
        this.pvpStatusConversionService = pvpStatusConversionService;
    }

    @Override
    @Cacheable(value = "pvpStatusesCache", key = "'allPvpStatuses'")
    public List<PvpStatusDto> cacheAllPvpStatuses() {
        List<PvpStatus> pvpStatuses = pvpStatusRepository.findAll();

        if(pvpStatuses.isEmpty()) {
            log.error("Unable to access pvp status data for caching");
            throw new ResourceNotFoundException("Unable to access pvp status data for caching");
        }

        return pvpStatuses.stream()
                .map(pvpStatusConversionService::convertToDto)
                .collect(Collectors.toList());
    }
}
