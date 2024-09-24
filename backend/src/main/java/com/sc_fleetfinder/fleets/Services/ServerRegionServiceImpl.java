package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.ServerRegionRepository;
import com.sc_fleetfinder.fleets.entities.ServerRegion;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServerRegionServiceImpl implements ServerRegionService {

    private final ServerRegionRepository serverRegionRepository;

    public ServerRegionServiceImpl(ServerRegionRepository serverRegionRepository) {
        super();
        this.serverRegionRepository = serverRegionRepository;
    }

    @Override
    public List<ServerRegion> getAllServerRegions() {
        return serverRegionRepository.findAll();
    }

    @Override
    public ServerRegion getServerRegionById(int id) {
        Optional<ServerRegion> optionalServerRegion = serverRegionRepository.findById(id);
        if(optionalServerRegion.isPresent()) {
            return optionalServerRegion.get();
        }
        else {
            throw new ResourceNotFoundException("Server region with id " + id + " not found");
        }
    }
}
