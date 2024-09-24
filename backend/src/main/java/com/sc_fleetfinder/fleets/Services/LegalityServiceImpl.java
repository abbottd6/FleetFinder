package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.entities.Legality;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LegalityServiceImpl implements LegalityService {

    private final LegalityRepository legalityRepository;

    public LegalityServiceImpl(LegalityRepository legalityRepository) {
        super();
        this.legalityRepository = legalityRepository;
    }

    @Override
    public List<Legality> getAllLegalities() {
        return legalityRepository.findAll();
    }

    @Override
    public Legality getLegalityById(int id) {
        Optional<Legality> optionalLegality = legalityRepository.findById(id);
        if (optionalLegality.isPresent()) {
            return optionalLegality.get();
        }
        else {
            throw new ResourceNotFoundException("Legality with id " + id + " not found");
        }
    }
}
