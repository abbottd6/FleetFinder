package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PvpStatusDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.PvpStatusCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PvpStatusServiceImpl implements PvpStatusService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final PvpStatusCachingService  pvpStatusCachingService;

    public PvpStatusServiceImpl(PvpStatusCachingService pvpStatusCachingService) {
        this.pvpStatusCachingService = pvpStatusCachingService;
    }

    @Override
    public List<PvpStatusDto> getAllPvpStatuses() {
        return pvpStatusCachingService.cacheAllPvpStatuses();
    }

    @Override
    public PvpStatusDto getPvpStatusById(Integer id) {
        List<PvpStatusDto> cachedPvpStatuses = pvpStatusCachingService.cacheAllPvpStatuses();

        return cachedPvpStatuses.stream()
                .filter(status -> status.getPvpStatusId().equals(id))
                .findFirst()
                .orElseGet(() -> {
                    log.error("Could not find pvp status with id {} in the cached pvp statuses", id);
                    throw new ResourceNotFoundException(id);
                });
    }
}
