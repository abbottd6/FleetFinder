package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.PlayStyleRepository;
import com.sc_fleetfinder.fleets.entities.PlayStyle;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayStyleServiceImpl implements PlayStyleService {

    private final PlayStyleRepository playStyleRepository;

    public PlayStyleServiceImpl(PlayStyleRepository playStyleRepository) {
        super();
        this.playStyleRepository = playStyleRepository;
    }

    @Override
    public List<PlayStyle> getAllPlayStyles() {
        return playStyleRepository.findAll();
    }

    @Override
    public PlayStyle getPlayStyleById(int id) {
        Optional<PlayStyle> optionalPlayStyle = playStyleRepository.findById(id);
        if(optionalPlayStyle.isPresent()) {
            return optionalPlayStyle.get();
        }
        else {
            throw new ResourceNotFoundException("Play style with id " + id + " not found");
        }
    }
}
