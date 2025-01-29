package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.entities.Legality;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LegalityServiceImpl implements LegalityService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final LegalityRepository legalityRepository;
    private final ModelMapper modelMapper;

    public LegalityServiceImpl(LegalityRepository legalityRepository) {
        super();
        this.legalityRepository = legalityRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    @Cacheable(value = "legalitiesCache", key = "'allLegalitiesCache'")
    public List<LegalityDto> getAllLegalities() {
        log.info("Caching test: getting all legalities");
        List<Legality> legalities = legalityRepository.findAll();

        if(legalities.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for legalities");
        }

        return legalities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public LegalityDto getLegalityById(Integer id) {
        Optional<Legality> optionalLegality = legalityRepository.findById(id);
        if (optionalLegality.isPresent()) {
            return convertToDto(optionalLegality.get());
        }
        else {
            throw new ResourceNotFoundException(id);
        }
    }

    public LegalityDto convertToDto(Legality entity) {
        return modelMapper.map(entity, LegalityDto.class);
    }

    public Legality convertToEntity(LegalityDto dto) {
        return modelMapper.map(dto, Legality.class);
    }
}
