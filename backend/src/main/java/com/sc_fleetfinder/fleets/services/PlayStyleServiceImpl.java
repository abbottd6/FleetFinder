package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PlayStyleRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlayStyleDto;
import com.sc_fleetfinder.fleets.entities.PlayStyle;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayStyleServiceImpl implements PlayStyleService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final PlayStyleRepository playStyleRepository;
    private final ModelMapper modelMapper;

    public PlayStyleServiceImpl(PlayStyleRepository playStyleRepository) {
        super();
        this.playStyleRepository = playStyleRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    @Cacheable(value = "playStylesCache", key = "'allPlayStyles'")
    public List<PlayStyleDto> getAllPlayStyles() {
        log.info("Caching test: getting all play styles");
        List<PlayStyle> playStyles = playStyleRepository.findAll();
        return playStyles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlayStyleDto getPlayStyleById(Integer id) {
        Optional<PlayStyle> optionalPlayStyle = playStyleRepository.findById(id);
        if(optionalPlayStyle.isPresent()) {
            return convertToDto(optionalPlayStyle.get());
        }
        else {
            throw new ResourceNotFoundException("Play style with id " + id + " not found");
        }
    }

    public PlayStyleDto convertToDto(PlayStyle entity) {
        return modelMapper.map(entity, PlayStyleDto.class);
    }

    public PlayStyle convertToEntity(PlayStyleDto dto) {
        return modelMapper.map(dto, PlayStyle.class);
    }
}
