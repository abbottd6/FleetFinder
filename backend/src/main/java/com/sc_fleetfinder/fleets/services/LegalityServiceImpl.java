package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.LegalityRepository;
import com.sc_fleetfinder.fleets.DTO.LegalityDto;
import com.sc_fleetfinder.fleets.entities.Legality;
import org.modelmapper.ModelMapper;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LegalityServiceImpl implements LegalityService {

    private final LegalityRepository legalityRepository;
    private final ModelMapper modelMapper;

    public LegalityServiceImpl(LegalityRepository legalityRepository) {
        super();
        this.legalityRepository = legalityRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<LegalityDto> getAllLegalities() {
        List<Legality> legalities = legalityRepository.findAll();
        return legalities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public LegalityDto getLegalityById(int id) {
        Optional<Legality> optionalLegality = legalityRepository.findById(id);
        if (optionalLegality.isPresent()) {
            return convertToDto(optionalLegality.get());
        }
        else {
            throw new ResourceNotFoundException("Legality with id " + id + " not found");
        }
    }

    public LegalityDto convertToDto(Legality entity) {
        return modelMapper.map(entity, LegalityDto.class);
    }

    public Legality convertToEntity(LegalityDto dto) {
        return modelMapper.map(dto, Legality.class);
    }
}
