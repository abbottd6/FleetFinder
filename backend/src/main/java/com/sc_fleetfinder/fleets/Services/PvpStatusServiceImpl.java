package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.PvpStatusRepository;
import com.sc_fleetfinder.fleets.entities.PvpStatus;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PvpStatusServiceImpl implements PvpStatusService {

    private final PvpStatusRepository pvpStatusRepository;

    public PvpStatusServiceImpl(PvpStatusRepository pvpStatusRepository) {
        super();
        this.pvpStatusRepository = pvpStatusRepository;
    }

    @Override
    public List<PvpStatus> getAllPvpStatuses() {
        return pvpStatusRepository.findAll();
    }

    @Override
    public PvpStatus getPvpStatusById(int id) {
        Optional<PvpStatus> optionalPvpStatus = pvpStatusRepository.findById(id);
        if (optionalPvpStatus.isPresent()) {
            return optionalPvpStatus.get();
        }
        else {
            throw new ResourceNotFoundException("Pvp status with id " + id + " not found");
        }
    }
}
